package work.soho.admin.biz.controller.guest;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.vo.AdminUserLoginVo;
import work.soho.admin.biz.config.AdminSysConfig;
import work.soho.admin.biz.domain.AdminUserLoginLog;
import work.soho.admin.biz.service.AdminConfigService;
import work.soho.admin.biz.service.AdminUserLoginLogService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IpUtils;
import work.soho.common.core.util.RequestUtil;
import work.soho.common.data.captcha.utils.CaptchaUtils;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 管理后台访客鉴权控制器。
 */
@Api(tags = "管理后台访客鉴权")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/guest/auth")
public class GuestAuthController {
    private static final String LOGIN_FAIL_USER_KEY_PREFIX = "admin:login:fail:user:";
    private static final String LOGIN_FAIL_IP_KEY_PREFIX = "admin:login:fail:ip:";
    private static final String USER_ROLE_ADMIN = "admin";
    private static final String REQUEST_HEADER_USER_AGENT = "User-Agent";

    private final TokenServiceImpl tokenService;
    private final AdminSysConfig adminSysConfig;
    private final AdminConfigService adminConfigService;
    private final AdminUserLoginLogService adminUserLoginLogService;
    private final StringRedisTemplate stringRedisTemplate;

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 获取登录页配置。
     *
     * @param username 用户名
     * @return 登录页配置
     */
    @GetMapping("/login/config")
    public R<HashMap<String, Object>> authConfig(@RequestParam(value = "username", required = false) String username) {
        HashMap<String, Object> config = new HashMap<>();
        config.put("useCaptcha", shouldUseCaptcha(username, IpUtils.getClientIp()));
        config.put("title", adminConfigService.getByKey("admin-front-title", String.class, "SOHO管理系统"));
        config.put("logo", adminConfigService.getByKey("admin-front-logo", String.class, "https://igogo-test.oss-cn-shenzhen.aliyuncs.com/upload/6e/d7/6d6ed76d7a1ea252d6e2616bc923299b66.png"));
        config.put("license", adminConfigService.getByKey("admin-front-footer-license", String.class, "Copyright © 2025 Liu Fang. Soho is open-source software licensed under GPL3 License."));
        return R.success(config);
    }

    /**
     * 管理员登录。
     *
     * @param adminUserLoginVo 登录参数
     * @return 登录结果与令牌
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody AdminUserLoginVo adminUserLoginVo) {
        String clientIp = IpUtils.getClientIp();
        String username = adminUserLoginVo.getUsername();
        boolean useCaptcha = shouldUseCaptcha(username, clientIp);
        if (useCaptcha && !isCaptchaValid(adminUserLoginVo.getCaptcha())) {
            return captchaRequiredResponse("请先完成验证码校验");
        }

        Authentication authentication;
        try {
            authentication = authenticateAdmin(adminUserLoginVo);
            clearLoginFailCounter(username, clientIp);
            if (useCaptcha) {
                CaptchaUtils.dropCaptcha();
            }
        } catch (Exception e) {
            log.warn("管理员登录失败 username={}", username, e);
            recordLoginFail(username, clientIp);
            if (shouldUseCaptcha(username, clientIp)) {
                return captchaRequiredResponse("登录失败次数过多，请输入验证码后重试");
            }
            return R.error("登录失败");
        }

        SohoUserDetails loginUser = (SohoUserDetails) authentication.getPrincipal();
        Map<String, Object> token = new HashMap<>(tokenService.createTokenInfo(loginUser));
        token.put("useCaptcha", false);
        saveLoginLog(loginUser, token);
        return R.success(token);
    }

    /**
     * 用户登出。
     *
     * @return 登出结果
     */
    @ApiOperation("用户登出")
    @GetMapping("/logout")
    public R<Boolean> logout() {
        return R.success(true);
    }

    /**
     * 执行管理员认证。
     *
     * @param loginVo 登录参数
     * @return 认证结果
     */
    private Authentication authenticateAdmin(AdminUserLoginVo loginVo) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginVo.getUsername(),
                loginVo.getPassword(),
                AuthorityUtils.createAuthorityList(USER_ROLE_ADMIN)
        );
        return authenticationManager.authenticate(authToken);
    }

    /**
     * 保存管理员登录成功日志。
     *
     * @param loginUser 登录用户
     * @param token 令牌信息
     */
    private void saveLoginLog(SohoUserDetails loginUser, Map<String, Object> token) {
        AdminUserLoginLog adminUserLoginLog = new AdminUserLoginLog();
        adminUserLoginLog.setAdminUserId(loginUser.getId());
        adminUserLoginLog.setClientIp(IpUtils.getClientIp());
        adminUserLoginLog.setCreatedTime(LocalDateTime.now());
        adminUserLoginLog.setToken(JSONUtil.toJsonStr(token));
        adminUserLoginLog.setClientUserAgent(RequestUtil.getHeader(REQUEST_HEADER_USER_AGENT));
        adminUserLoginLogService.save(adminUserLoginLog);
    }

    /**
     * 判断当前请求是否需要验证码。
     *
     * @param username 用户名
     * @param clientIp 客户端 IP
     * @return 是否需要验证码
     */
    private boolean shouldUseCaptcha(String username, String clientIp) {
        if (Boolean.TRUE.equals(adminSysConfig.getAdminLoginCaptchaEnable())) {
            return true;
        }
        if (!Boolean.TRUE.equals(adminSysConfig.getAdminLoginAutoCaptchaEnable())) {
            return false;
        }
        return getLoginFailCount(buildLoginFailUserKey(username)) >= adminSysConfig.getAdminLoginCaptchaUserFailThreshold()
                || getLoginFailCount(buildLoginFailIpKey(clientIp)) >= adminSysConfig.getAdminLoginCaptchaIpFailThreshold();
    }

    /**
     * 校验验证码是否有效。
     *
     * @param captcha 验证码
     * @return 是否有效
     */
    private boolean isCaptchaValid(String captcha) {
        return StringUtils.hasText(captcha) && CaptchaUtils.checking(captcha);
    }

    /**
     * 记录登录失败次数。
     *
     * @param username 用户名
     * @param clientIp 客户端 IP
     */
    private void recordLoginFail(String username, String clientIp) {
        incrementLoginFailCounter(buildLoginFailUserKey(username));
        incrementLoginFailCounter(buildLoginFailIpKey(clientIp));
    }

    /**
     * 清理登录成功后的失败计数。
     *
     * @param username 用户名
     * @param clientIp 客户端 IP
     */
    private void clearLoginFailCounter(String username, String clientIp) {
        deleteLoginFailCounter(buildLoginFailUserKey(username));
        deleteLoginFailCounter(buildLoginFailIpKey(clientIp));
    }

    /**
     * 构建用户名失败计数 key。
     *
     * @param username 用户名
     * @return Redis key
     */
    private String buildLoginFailUserKey(String username) {
        return LOGIN_FAIL_USER_KEY_PREFIX + normalizeKeyPart(username);
    }

    /**
     * 构建 IP 失败计数 key。
     *
     * @param clientIp 客户端 IP
     * @return Redis key
     */
    private String buildLoginFailIpKey(String clientIp) {
        return LOGIN_FAIL_IP_KEY_PREFIX + normalizeKeyPart(clientIp);
    }

    /**
     * 规范化 Redis key 片段。
     *
     * @param value 原始值
     * @return 规范化后的值
     */
    private String normalizeKeyPart(String value) {
        return value == null || value.trim().isEmpty() ? "unknown" : value.trim().toLowerCase();
    }

    /**
     * 递增登录失败计数并刷新过期时间。
     *
     * @param key Redis key
     */
    private void incrementLoginFailCounter(String key) {
        Long current = stringRedisTemplate.opsForValue().increment(key);
        if (current != null && current > 0) {
            stringRedisTemplate.expire(key, adminSysConfig.getAdminLoginCaptchaFailWindowMinutes(), TimeUnit.MINUTES);
        }
    }

    /**
     * 获取登录失败次数。
     *
     * @param key Redis key
     * @return 失败次数
     */
    private long getLoginFailCount(String key) {
        String count = stringRedisTemplate.opsForValue().get(key);
        if (count == null) {
            return 0L;
        }
        try {
            return Long.parseLong(count);
        } catch (NumberFormatException e) {
            log.warn("解析登录失败次数异常 key={}, count={}", key, count);
            return 0L;
        }
    }

    /**
     * 删除登录失败计数。
     *
     * @param key Redis key
     */
    private void deleteLoginFailCounter(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 构建需要验证码的错误响应。
     *
     * @param message 错误消息
     * @return 错误响应
     */
    private R<Map<String, Object>> captchaRequiredResponse(String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("useCaptcha", true);
        return R.error(5002, message, payload);
    }
}
