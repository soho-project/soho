package work.soho.admin.biz.controller;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.biz.config.AdminSysConfig;
import work.soho.admin.biz.service.AdminConfigService;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.admin.biz.domain.AdminUserLoginLog;
import work.soho.admin.biz.service.AdminUserLoginLogService;
import work.soho.admin.api.vo.AdminUserLoginVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IpUtils;
import work.soho.common.core.util.RequestUtil;
import work.soho.common.data.captcha.utils.CaptchaUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户鉴权")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final TokenServiceImpl tokenService;
    private final AdminSysConfig adminSysConfig;
    private final AdminConfigService adminConfigService;
    private final AdminUserLoginLogService adminUserLoginLogService;
    @Resource
    private AuthenticationManager authenticationManager;

    private static final String USER_ROLE_ADMIN = "admin";
    private static final String REQUEST_HEADER_USER_AGENT = "User-Agent";

    @GetMapping("/login/config")
    public R<HashMap<String, Object>> authConfig() {
        HashMap<String, Object> config = new HashMap<>();
        config.put("useCaptcha", adminSysConfig.getAdminLoginCaptchaEnable());
        config.put("title", adminConfigService.getByKey("admin-front-title", String.class, "SOHO管理系统"));
        config.put("logo", adminConfigService.getByKey("admin-front-logo", String.class, "https://igogo-test.oss-cn-shenzhen.aliyuncs.com/upload/6e/d7/6d6ed76d7a1ea252d6e2616bc923299b66.png"));
        config.put("license", adminConfigService.getByKey("admin-front-footer-license", String.class, "Copyright © 2025 Liu Fang. Soho is open-source software licensed under GPL3 License."));
        return R.success(config);
    }

    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    public R<Map<String, String>> login(@RequestBody AdminUserLoginVo adminUserLoginVo) {
        boolean useCaptcha = adminSysConfig.getAdminLoginCaptchaEnable();
        if (useCaptcha && !CaptchaUtils.checking(adminUserLoginVo.getCaptcha())) {
            return R.error("验证码错误");
        }

        Authentication authentication;
        try {
            authentication = authenticateAdmin(adminUserLoginVo);
            if (useCaptcha) {
                CaptchaUtils.dropCaptcha();
            }
        } catch (Exception e) {
            log.warn("管理员登录失败 username={}", adminUserLoginVo.getUsername(), e);
            return R.error("登录失败");
        }

        SohoUserDetails loginUser = (SohoUserDetails) authentication.getPrincipal();
        Map<String, String> token = tokenService.createTokenInfo(loginUser);
        saveLoginLog(loginUser, token);
        return R.success(token);
    }

    private Authentication authenticateAdmin(AdminUserLoginVo loginVo) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginVo.getUsername(),
                loginVo.getPassword(),
                AuthorityUtils.createAuthorityList(USER_ROLE_ADMIN)
        );
        // 该方法会调用对应角色的 UserDetailsService.loadUserByUsername
        return authenticationManager.authenticate(authToken);
    }

    private void saveLoginLog(SohoUserDetails loginUser, Map<String, String> token) {
        AdminUserLoginLog adminUserLoginLog = new AdminUserLoginLog();
        adminUserLoginLog.setAdminUserId(loginUser.getId());
        adminUserLoginLog.setClientIp(IpUtils.getClientIp());
        adminUserLoginLog.setCreatedTime(LocalDateTime.now());
        adminUserLoginLog.setToken(JSONUtil.toJsonStr(token));
        adminUserLoginLog.setClientUserAgent(RequestUtil.getHeader(REQUEST_HEADER_USER_AGENT));
        adminUserLoginLogService.save(adminUserLoginLog);
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
