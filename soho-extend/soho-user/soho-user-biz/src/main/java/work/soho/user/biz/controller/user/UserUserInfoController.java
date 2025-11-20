package work.soho.user.biz.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.request.ChangePasswordRequest;
import work.soho.user.api.request.ChangePhoneRequest;
import work.soho.user.api.request.SendNewPhoneSmsRequest;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;
import work.soho.user.biz.service.UserSmsService;

import java.util.List;

@Log4j2
@Api(tags = "用户信息")
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/user/userInfo" )
public class UserUserInfoController {
    private final UserInfoService userInfoService;
    private final AdminDictApiService adminDictApiService;
    private final UserSmsService userSmsService;

    /**
     * 获取用户信息
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping
    public R<UserInfo> getUserInfo(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        return R.success(userInfoService.getById(sohoUserDetails.getId()));
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    public R<UserInfo> updateUserInfo(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @RequestBody UserInfo info) {
        UserInfo userInfo = userInfoService.getById(sohoUserDetails.getId());
        userInfo.setAvatar(info.getAvatar());
        userInfo.setNickname(info.getNickname());
        userInfo.setEmail(info.getEmail());
        userInfo.setAge(info.getAge());
        userInfo.setSex(info.getSex());
        userInfoService.updateById(userInfo);
        return R.success(userInfo);
    }

    /**
     * 获取 用户信息 level 字典选项
     *
     * @return
     */
    @GetMapping("/queryLevelOptions")
    public R<List<OptionVo<Integer, String>>> queryLevelOptions(){
        return R.success(adminDictApiService.getOptionsByCode("user-info-level"));
    }

    @ApiOperation("用户头像上传接口")
    @PostMapping("/uploadAvatar")
    public Object uploadAvatar(@RequestParam(value = "avatar") MultipartFile file) {
        try {
            System.out.println(file);
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
            if(!mimeType.getType().equals("image")) {
                return R.error("请传递正确的图片格式");
            }
            System.out.println(file.getContentType());
            String url = UploadUtils.upload("user/avatar", file);
            return R.success(url);
        } catch (Exception ioException) {
            log.error(ioException.toString());
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }

    /**
     * 修改密码
     *
     * @param request
     * @param sohoUserDetails
     * @return
     */
    @ApiOperation("修改密码")
    @PutMapping("/changePassword")
    public R changePassword(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @RequestBody ChangePasswordRequest request) {
        if(!request.getNewPassword().equals(request.getConfirmPassword())) {
            return R.error("密码不一致");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if(request.getOldPassword()!= null) {
            if(!bCryptPasswordEncoder.matches(request.getOldPassword(), sohoUserDetails.getPassword())) {
                return R.error("旧密码错误");
            }
        } else if(!userSmsService.verifySmsCaptcha(sohoUserDetails.getId(), request.getCaptcha())) {
            return R.error("验证码错误");
        }

        // 获取用户
        UserInfo userInfo = userInfoService.getById(sohoUserDetails.getId());
        userInfo.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        return R.success(userInfoService.updateById(userInfo));
    }

    /**
     * 给用户新手机号发送验证码
     *
     */
    @ApiOperation("发送验证码")
    @PostMapping("/sendNewPhoneSms")
    public R sendNewPhoneSms(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @RequestBody SendNewPhoneSmsRequest request) {
        try {
            userSmsService.sendSmsCaptchaByPhone(userInfoService.getById(sohoUserDetails.getId()).getPhone());
            return R.success();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 修改手机号
     *
     * @param request
     * @param sohoUserDetails
     * @return
     */
    @ApiOperation("修改手机号")
    @PutMapping("/changePhone")
    public R<Boolean> changePhone(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @RequestBody ChangePhoneRequest request) {
        // 验证验证码是否正确
        if(!userSmsService.verifySmsCaptcha(sohoUserDetails.getId(), request.getCaptcha())) {
            return R.error("原手机验证码错误");
        }

        // 验证新手机验证码是否正确
        if(!userSmsService.verifySmsCaptchaByPhone(request.getNewPhone(), request.getNewPhoneCaptcha())) {
            return R.error("新手机号验证码错误");
        }

        UserInfo userInfo = userInfoService.getById(sohoUserDetails.getId());
        userInfo.setPhone(request.getNewPhone());
        return R.success(userInfoService.updateById(userInfo));
    }

    /**
     * 发送认证验证码
     *
     * @param sohoUserDetails
     * @return
     */
    @ApiOperation("发送验证码")
    @PostMapping("/sendSms")
    public R sendSms(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            userSmsService.sendSmsCaptcha(sohoUserDetails.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("发送失败");
        }
        return R.success();
    }
}
