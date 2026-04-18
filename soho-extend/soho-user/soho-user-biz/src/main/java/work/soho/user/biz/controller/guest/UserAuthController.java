package work.soho.user.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.service.AdminConfigApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.StringUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.data.captcha.utils.CaptchaUtils;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.request.ResetPasswordByEmailRequest;
import work.soho.user.api.request.SendRegisterEmailCodeRequest;
import work.soho.user.api.request.SendNewPhoneSmsRequest;
import work.soho.user.api.vo.UserLoginVo;
import work.soho.user.api.vo.UserRegisterVo;
import work.soho.user.biz.config.UserSysConfig;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.enums.UserInfoEnums;
import work.soho.user.biz.service.UserEmailCodeService;
import work.soho.user.biz.service.UserInfoService;
import work.soho.user.biz.service.UserSmsService;
import work.soho.user.biz.vo.UserSimpleRegisterVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Pattern;


@Api(tags = "会员鉴权")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/guest/user/auth")
public class UserAuthController {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z_-][A-Za-z0-9_-]*$");

    private final TokenServiceImpl tokenService;
    private final AdminConfigApiService adminSysConfig;
    private final UserInfoService userInfoService;
    private final StringRedisTemplate redisTemplate;
    private final UserSysConfig userSysConfig;
    private final UserSmsService userSmsService;
    private final UserEmailCodeService userEmailCodeService;

    @Autowired
    private Map<String, SohoUserDetailsService> detailsServiceMap;

    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    public Object login(@RequestBody UserLoginVo userLoginVo) {
        //查询用户信息
        if(userLoginVo.getUsername() == null || userLoginVo.getUsername().isEmpty()
                || userLoginVo.getPassword() == null || userLoginVo.getPassword().isEmpty()) {
            return R.error("请检查用户名或密码");
        }

        LambdaQueryWrapper<UserInfo> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(UserInfo::getUsername, userLoginVo.getUsername());
        lambdaQuery.or().eq(UserInfo::getPhone, userLoginVo.getUsername());
        lambdaQuery.or().eq(UserInfo::getEmail, userLoginVo.getUsername());

        UserInfo userInfo = userInfoService.getOne(lambdaQuery);
        if(userInfo == null) {
            return R.error("用户不存在");
        }

        // 验证密码是否正确
        if(!new BCryptPasswordEncoder().matches(userLoginVo.getPassword(), userInfo.getPassword())) {
            return R.error("密码错误");
        }

        Boolean userCaptcha = adminSysConfig.getByKey("admin.login.captcha.enable", Boolean.class, false);
        // 检查是否开启验证码
        if(userCaptcha) {
            if(userLoginVo.getCaptcha() == null || userLoginVo.getCaptcha().isEmpty()
                    ||  !CaptchaUtils.checking(userLoginVo.getCaptcha())) {
                return R.error("请检查验证码");
            }
        }

        Object res =  commonLogin(userInfo);
        if(userCaptcha) {
            CaptchaUtils.dropCaptcha();
        }
        return res;
    }

    private Object commonLogin(UserInfo userInfo) {
        Authentication authentication = null;
        SohoUserDetails loginUser = null;
        try{
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
//            authentication = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(userInfo.getUsername(), userLoginVo.getPassword(), AuthorityUtils.createAuthorityList("user") ));
            for (SohoUserDetailsService service : detailsServiceMap.values()) {
                if(service.getUserRoleName().equals("user")) {
                    loginUser = service.loadUserByUsername(userInfo.getUsername());
                    break;
                }
            }
//            if(authentication == null) {
//                return R.error("登录失败");
//            }

        } catch (Exception e) {
            e.printStackTrace();
            return R.error("登录失败");
        }

//        SohoUserDetails loginUser = (SohoUserDetails) authentication.getPrincipal();
        Map<String, String> token = tokenService.createTokenInfo(loginUser);
        return R.success(token);
    }

    /**
     * 手机登录
     */
    @ApiOperation("手机登录")
    @PostMapping(value = "mobileLogin")
    public Object mobileLogin(@RequestBody UserLoginVo userLoginVo) {
        System.out.println(userLoginVo);
        if(userLoginVo.getUsername() == null || userLoginVo.getUsername().isEmpty()
                || userLoginVo.getCaptcha() == null || userLoginVo.getCaptcha().isEmpty()) {
            return R.error("请检查手机号");
        }

        LambdaQueryWrapper<UserInfo> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(UserInfo::getUsername, userLoginVo.getUsername());
        lambdaQuery.or().eq(UserInfo::getPhone, userLoginVo.getUsername());
        lambdaQuery.or().eq(UserInfo::getEmail, userLoginVo.getUsername());

        UserInfo userInfo = userInfoService.getOne(lambdaQuery);
        if(userInfo == null) {
            return R.error("用户不存在");
        }

        // 检查验证码是否正确
        if(!userSmsService.verifySmsCaptchaByPhone(userInfo.getPhone(), userLoginVo.getCaptcha())) {
            return R.error("手机验证码错误");
        }

        Object res =  commonLogin(userInfo);
        return res;
    }

    /**
     * 发送短信验证码
     *
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation("发送短信验证码")
    @PostMapping(value = "sendSms")
    public R sendSms(@RequestBody SendNewPhoneSmsRequest request) throws Exception {
        if(request.getPhone() == null || request.getPhone().isEmpty()) {
            return R.error();
        }

        userSmsService.sendSmsCaptchaByPhone(request.getPhone());
        return R.success();
    }

    /**
     * 发送注册邮箱验证码。
     *
     * @param request 邮箱请求
     * @return 发送结果
     */
    @ApiOperation("发送注册邮箱验证码")
    @PostMapping(value = "sendEmailCode")
    public R sendEmailCode(@RequestBody SendRegisterEmailCodeRequest request) {
        if (request == null || StringUtils.isBlank(request.getEmail())) {
            return R.error("邮箱不能为空");
        }
        userEmailCodeService.sendRegisterEmailCode(request.getEmail());
        return R.success();
    }

    /**
     * 发送找回密码邮箱验证码。
     *
     * @param request 邮箱请求
     * @return 发送结果
     */
    @ApiOperation("发送找回密码邮箱验证码")
    @PostMapping(value = "sendResetPasswordEmailCode")
    public R sendResetPasswordEmailCode(@RequestBody SendRegisterEmailCodeRequest request) {
        if (request == null || StringUtils.isBlank(request.getEmail())) {
            return R.error("邮箱不能为空");
        }
        String email = request.getEmail().trim();
        if (!isEmailExists(email)) {
            return R.error("邮箱未注册");
        }
        userEmailCodeService.sendResetPasswordEmailCode(email);
        return R.success();
    }

    /**
     * 获取图形验证码
     *
     * 返回一个图片
     *
     * @param response
     * @throws IOException
     */
    @ApiOperation("获取验证码")
    @PostMapping("/captcha")
    public void defaultKaptcha(HttpServletResponse response) throws IOException {
        try {
            CaptchaUtils.createAndSend();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * 注册用户
     *
     * @param userRegisterVo
     * @return
     */
    @PostMapping("register")
    public R<UserInfo> register(@RequestBody UserRegisterVo userRegisterVo) {
        String email = StringUtils.isBlank(userRegisterVo.getEmail()) ? null : userRegisterVo.getEmail().trim();
        userRegisterVo.setEmail(email);

        // 检查用户是否存在
        UserInfo oldUser = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, userRegisterVo.getPhone()));
        if(oldUser != null) {
            return R.error("用户已存在");
        }

        Boolean isDev = userSysConfig.getLoginDev();
        // 检查用户是否通过短信验证
        String code = userRegisterVo.getVerifyCode();
        if(code == null || code.isEmpty()) {
            if(!isDev) {
                return R.error("请输入验证码");
            }
        }

        // 验证验证码是否正确
        if(!userSmsService.verifySmsCaptchaByPhone(userRegisterVo.getPhone(), code)) {
            return R.error("验证码错误");
        }

        // 如果传递了邮箱，必须同时校验邮箱验证码
        if (StringUtils.isNotBlank(userRegisterVo.getEmail())) {
            if (isEmailExists(userRegisterVo.getEmail())) {
                return R.error("邮箱已存在");
            }
            if (StringUtils.isBlank(userRegisterVo.getEmailVerifyCode())) {
                return R.error("请输入邮箱验证码");
            }
            if (!userEmailCodeService.verifyRegisterEmailCode(userRegisterVo.getEmail(), userRegisterVo.getEmailVerifyCode())) {
                return R.error("邮箱验证码错误");
            }
        }


        String password = userRegisterVo.getPassword();
        if(password == null || password.isEmpty()) {
            return R.error("请输入密码");
        }

        // 确定用户名
        String username = userRegisterVo.getUsername();
        if(username == null || username.trim().isEmpty()) {
            username = "P"+userRegisterVo.getPhone();
        } else {
            username = username.trim();
            if (!isValidUsername(username)) {
                return R.error("用户名格式错误；只允许字母、数字、下划线、中划线，且数字不能作为首字符");
            }
        }
        String nickname = userRegisterVo.getNickname();
        if(nickname == null || nickname.isEmpty()) {
            nickname = username;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setCode(IDGeneratorUtils.snowflake().toString());

        if(userRegisterVo.getInviteCode() != null) {
            LambdaQueryWrapper<UserInfo> lambdaQuery = new LambdaQueryWrapper<>();
            lambdaQuery.eq(UserInfo::getCode, userRegisterVo.getInviteCode());
            UserInfo isUserInfo = userInfoService.getOne(lambdaQuery);
            if(isUserInfo != null) {
                userInfo.setReferrerId(isUserInfo.getId());
            }
        }

        password = new BCryptPasswordEncoder().encode(password);
        userInfo.setUsername(username);
        userInfo.setEmail(userRegisterVo.getEmail());
        userInfo.setNickname(nickname);
        userInfo.setPassword(password);
        userInfo.setId(null);
        userInfo.setPhone(userRegisterVo.getPhone());
        userInfo.setCreatedTime(LocalDateTime.now());
        userInfo.setUpdatedTime(LocalDateTime.now());
        userInfo.setAvatar(userSysConfig.getDefaultAvatar());

        // 检查用户是否已经存在
        if(userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, username)) != null) {
            return R.error("用户名已存在");
        }

        // 检查手机号是否已经存在
        if(userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, userRegisterVo.getPhone())) != null) {
            return R.error("手机号已存在");
        }

        userInfoService.register(userInfo);
        return R.success(userInfo);
    }

    /**
     * 通过邮箱验证码找回密码。
     *
     * @param request 重置密码请求
     * @return 处理结果
     */
    @ApiOperation("通过邮箱验证码找回密码")
    @PostMapping("resetPasswordByEmail")
    public R<Boolean> resetPasswordByEmail(@RequestBody ResetPasswordByEmailRequest request) {
        if (request == null || StringUtils.isBlank(request.getEmail())) {
            return R.error("邮箱不能为空");
        }
        if (StringUtils.isBlank(request.getEmailVerifyCode())) {
            return R.error("请输入邮箱验证码");
        }
        if (StringUtils.isBlank(request.getNewPassword())) {
            return R.error("请输入新密码");
        }
        if (StringUtils.isBlank(request.getConfirmPassword())) {
            return R.error("请输入确认密码");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return R.error("密码不一致");
        }

        String email = request.getEmail().trim();
        UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getEmail, email));
        if (userInfo == null) {
            return R.error("邮箱未注册");
        }
        if (!userEmailCodeService.verifyResetPasswordEmailCode(email, request.getEmailVerifyCode())) {
            return R.error("邮箱验证码错误");
        }

        userInfo.setPassword(new BCryptPasswordEncoder().encode(request.getNewPassword()));
        userInfo.setUpdatedTime(LocalDateTime.now());
        return R.success(userInfoService.updateById(userInfo));
    }

    /**
     * 用户名密码注册
     */
    @ApiOperation("用户名密码注册")
    @PostMapping("simpleRegister")
    public R<UserInfo> simpleRegister(@RequestBody UserSimpleRegisterVo userSimpleRegisterVo) {
        String email = StringUtils.isBlank(userSimpleRegisterVo.getEmail()) ? null : userSimpleRegisterVo.getEmail().trim();
        userSimpleRegisterVo.setEmail(email);

        String username = userSimpleRegisterVo.getUsername();
        if(username == null || username.trim().isEmpty()) {
            return R.error("请输入用户名");
        }
        username = username.trim();
        if (!isValidUsername(username)) {
            return R.error("用户名格式错误；只允许字母、数字、下划线、中划线，且数字不能作为首字符");
        }

        String password = userSimpleRegisterVo.getPassword();
        if(password == null || password.isEmpty()) {
            return R.error("请输入密码");
        }

        if(userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, username)) != null) {
            return R.error("用户名已存在");
        }

        // 如果传递了邮箱，必须同时校验邮箱验证码
        if (StringUtils.isNotBlank(userSimpleRegisterVo.getEmail())) {
            if (isEmailExists(userSimpleRegisterVo.getEmail())) {
                return R.error("邮箱已存在");
            }
            if (StringUtils.isBlank(userSimpleRegisterVo.getEmailVerifyCode())) {
                return R.error("请输入邮箱验证码");
            }
            if (!userEmailCodeService.verifyRegisterEmailCode(userSimpleRegisterVo.getEmail(), userSimpleRegisterVo.getEmailVerifyCode())) {
                return R.error("邮箱验证码错误");
            }
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setCode(IDGeneratorUtils.snowflake().toString());
        userInfo.setUsername(username);
        userInfo.setNickname(username);
        userInfo.setEmail(userSimpleRegisterVo.getEmail());
        userInfo.setPassword(new BCryptPasswordEncoder().encode(password));
        userInfo.setStatus(UserInfoEnums.Status.NORMAL.getId());
        userInfo.setAvatar(userSysConfig.getDefaultAvatar());
        userInfo.setCreatedTime(LocalDateTime.now());
        userInfo.setUpdatedTime(LocalDateTime.now());
        userInfo.setId(null);

        userInfoService.register(userInfo);
        return R.success(userInfo);
    }

    /**
     * 检查邮箱是否已被注册。
     *
     * @param email 邮箱
     * @return true: 已存在
     */
    private boolean isEmailExists(String email) {
        return userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getEmail, email)) != null;
    }

    /**
     * 检查用户名格式是否有效。
     *
     * 规则：只允许字母、数字、下划线和中划线，且数字不能作为首字符。
     *
     * @param username 用户名
     * @return true: 格式有效
     */
    private boolean isValidUsername(String username) {
        return StringUtils.isNotBlank(username) && USERNAME_PATTERN.matcher(username).matches();
    }
}
