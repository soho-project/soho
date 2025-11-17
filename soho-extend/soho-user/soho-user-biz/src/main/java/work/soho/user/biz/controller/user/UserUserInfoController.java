package work.soho.user.biz.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;

import java.util.List;

@Log4j2
@Api(tags = "用户信息")
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/user/userInfo" )
public class UserUserInfoController {
    private final UserInfoService userInfoService;
    private final AdminDictApiService adminDictApiService;

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
}
