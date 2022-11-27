package work.soho.admin.controller;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.domain.AdminUserLoginLog;
import work.soho.admin.service.AdminConfigService;
import work.soho.admin.service.AdminUserLoginLogService;
import work.soho.admin.service.impl.TokenServiceImpl;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.api.admin.vo.AdminUserLoginVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IpUtils;
import work.soho.common.core.util.RequestUtil;
import work.soho.common.data.captcha.utils.CaptchaUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户鉴权")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AdminConfigService sohoConfig;
    private final TokenServiceImpl tokenService;
    private final AdminConfigService adminConfigService;
    private final AdminUserLoginLogService adminUserLoginLogService;
    @Resource
    private AuthenticationManager authenticationManager;

    private static final String LOGIN_USE_CAPTCHA = "login_use_captcha";

    @GetMapping("/login/config")
    public R<HashMap<String, Object>> authConfig() {
        HashMap<String, Object> config = new HashMap<>();
        config.put("useCaptcha", sohoConfig.getByKey("use_captcha", Boolean.class, Boolean.TRUE));
        return R.success(config);
    }

    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    public Object login(@RequestBody AdminUserLoginVo adminUserLoginVo) {
        Authentication authentication = null;
        try{
            Boolean useCaptcha = adminConfigService.getByKey(LOGIN_USE_CAPTCHA, Boolean.class, Boolean.TRUE);
            if(useCaptcha && !CaptchaUtils.checking(adminUserLoginVo.getCaptcha())) {
               //检查验证码是否正确
               return R.error("验证码错误");
            }
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(adminUserLoginVo.getUsername(), adminUserLoginVo.getPassword()));
            //登录成功，删除验证码
            if(useCaptcha) {
                CaptchaUtils.dropCaptcha();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return R.error("登录失败");
        }
        UserDetailsServiceImpl.UserDetailsImpl loginUser = (UserDetailsServiceImpl.UserDetailsImpl) authentication.getPrincipal();
        Map<String, String> token = tokenService.createTokenInfo(loginUser);
        //创建登录日志
        AdminUserLoginLog adminUserLoginLog = new AdminUserLoginLog();
        adminUserLoginLog.setAdminUserId(loginUser.getId());
        adminUserLoginLog.setClientIp(IpUtils.getClientIp());
        adminUserLoginLog.setCreatedTime(new Date());
        adminUserLoginLog.setToken(JSONUtil.toJsonStr(token));
        adminUserLoginLog.setClientUserAgent(RequestUtil.getHeader("User-Agent"));
        adminUserLoginLogService.save(adminUserLoginLog);
        return R.success(token);
    }

    /**
     * 用户登出
     *
     * @return
     */
    @ApiOperation("用户登出")
    @GetMapping("/logout")
    public R<Boolean> logout() {
        //nothing
        return R.success(true);
    }
}
