package work.soho.open.biz.controller.guest;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.api.result.OpenErrorCode;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.enums.OpenAppEnums;
import work.soho.open.biz.service.OpenAppService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Api(value = "Oauth", tags = "开放平台Oauth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/guest/oauth" )
public class GuestOauthController {
    private static final String CODE_PREFIX = "open:oauth:code:";
    private static final long CODE_TTL_SECONDS = 300L;

    private final OpenAppService openAppService;
    private final TokenServiceImpl tokenService;
    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response,
                          @RequestParam(value = "response_type", required = false) String responseType,
                          @RequestParam(value = "client_id", required = false) String clientId,
                          @RequestParam(value = "app_key", required = false) String appKey,
                          @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                          @RequestParam(value = "state", required = false) String state,
                          @CookieValue(name = "userToken", required = false) String userToken
                          ) throws IOException {
        // 获取当前授权用户信息
        SohoUserDetails userDetails = tokenService.getUserDetailsByJwtToken(userToken);
        if (userDetails == null) {
            sendRedirectWithError(response, redirectUri, "access_denied", state);
            return;
        }

        if (StringUtils.isBlank(responseType)) {
            responseType = "code";
        }
        if (!"code".equals(responseType)) {
            sendRedirectWithError(response, redirectUri, "unsupported_response_type", state);
            return;
        }
        if (StringUtils.isBlank(redirectUri)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing redirect_uri");
            return;
        }

        String finalAppKey = firstNonBlank(clientId, appKey);
        if (StringUtils.isBlank(finalAppKey)) {
            sendRedirectWithError(response, redirectUri, "invalid_client", state);
            return;
        }
        OpenApp openApp = openAppService.getOpenAppByKey(finalAppKey);
        if (openApp == null) {
            sendRedirectWithError(response, redirectUri, "invalid_client", state);
            return;
        }
        if (openApp.getStatus() == null || openApp.getStatus() != OpenAppEnums.Status.ACTIVE.getId()) {
            sendRedirectWithError(response, redirectUri, "access_denied", state);
            return;
        }
        if (!isRedirectUriAllowed(openApp, redirectUri)) {
            sendRedirectWithError(response, redirectUri, "invalid_redirect_uri", state);
            return;
        }
        if (openApp.getUserId() == null) {
            sendRedirectWithError(response, redirectUri, "access_denied", state);
            return;
        }

        String code = IDGeneratorUtils.uuid32();
        String encodedRedirect = Base64.getUrlEncoder().encodeToString(redirectUri.getBytes(StandardCharsets.UTF_8));
        String value = finalAppKey + "|" + userDetails.getId() + "|" + userDetails.getUsername() + "|" + encodedRedirect + "|" + responseType;
        stringRedisTemplate.opsForValue().set(CODE_PREFIX + code, value, CODE_TTL_SECONDS, TimeUnit.SECONDS);

        String target = appendQuery(redirectUri, "code", code);
        if (StringUtils.isNotBlank(state)) {
            target = appendQuery(target, "state", state);
        }
        response.sendRedirect(target);
    }

    @GetMapping("/2.0")
    public R<Map<String, String>> oauth2(
            @RequestParam(value = "grant_type", required = false) String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "app_key", required = false) String appKey,
            @RequestParam(value = "app_secret", required = false) String appSecret,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri
            ) {
        if (StringUtils.isBlank(grantType)) {
            grantType = "client_credentials";
        }
        String finalAppKey = firstNonBlank(clientId, appKey);
        String finalAppSecret = firstNonBlank(clientSecret, appSecret);
        if (StringUtils.isBlank(finalAppKey) || StringUtils.isBlank(finalAppSecret)) {
            return R.error(OpenErrorCode.MISSING_THE_NECESSARY_REQUEST);
        }

        if ("authorization_code".equals(grantType)) {
            if (StringUtils.isBlank(code)) {
                return R.error(OpenErrorCode.MISSING_THE_NECESSARY_REQUEST);
            }
            OpenApp openApp = validateApp(finalAppKey, finalAppSecret);
            if (openApp == null) {
                return R.error(OpenErrorCode.PARAM_ERROR_CODE);
            }
            String value = stringRedisTemplate.opsForValue().get(CODE_PREFIX + code);
            if (StringUtils.isBlank(value)) {
                return R.error(OpenErrorCode.PARAM_ERROR_CODE);
            }
            stringRedisTemplate.delete(CODE_PREFIX + code);

            String[] parts = value.split("\\|", 5);
            if (parts.length < 3) {
                return R.error(OpenErrorCode.PARAM_ERROR_CODE);
            }
            String storedAppKey = parts[0];
            SohoUserDetails userDetails = new SohoUserDetails();
            userDetails.setId(Long.parseLong(parts[1]));
            userDetails.setUsername(parts[2]);
            userDetails.setClaims(new HashMap<>());
            userDetails.setAuthorities(AuthorityUtils.createAuthorityList("user"));

            String storedRedirect = new String(Base64.getUrlDecoder().decode(parts[3]), StandardCharsets.UTF_8);
            if (!finalAppKey.equals(storedAppKey)) {
                return R.error(OpenErrorCode.PARAM_ERROR_CODE);
            }
            if (StringUtils.isNotBlank(redirectUri) && !redirectUri.equals(storedRedirect)) {
                return R.error(OpenErrorCode.PARAM_ERROR_CODE);
            }
            return R.success(buildTokenResponse(openApp, userDetails));
        }

        if (!"client_credentials".equals(grantType)) {
            return R.error(OpenErrorCode.PARAM_ERROR_CODE);
        }
        OpenApp openApp = validateApp(finalAppKey, finalAppSecret);
        if (openApp == null) {
            return R.error(OpenErrorCode.PARAM_ERROR_CODE);
        }
        return R.success(buildTokenResponse(openApp, null));
    }

    private OpenApp validateApp(String appKey, String appSecret) {
        OpenApp openApp = openAppService.getOpenAppByKey(appKey);
        if (openApp == null) {
            return null;
        }
        if (openApp.getStatus() == null || openApp.getStatus() != OpenAppEnums.Status.ACTIVE.getId()) {
            return null;
        }
        if (!appSecret.equals(openApp.getAppSecret())) {
            return null;
        }
        if (openApp.getUserId() == null) {
            return null;
        }
        return openApp;
    }

    private Map<String, String> buildTokenResponse(OpenApp openApp, SohoUserDetails userDetails) {
        SohoUserDetails loginUser = new SohoUserDetails();
        if(userDetails != null) {
            // 授权码模式
            loginUser.setId(userDetails.getId());
            loginUser.setUsername(userDetails.getUsername());
            loginUser.setAuthorities(AuthorityUtils.createAuthorityList("openUser"));
        } else {
            // 客户端凭证模式
            loginUser.setId(openApp.getId());
            loginUser.setUsername(openApp.getAppKey());
            loginUser.setAuthorities(AuthorityUtils.createAuthorityList("openApp"));
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("appId", openApp.getId());
        params.put("appKey", openApp.getAppKey());
        Map<String, String> tokenInfo = tokenService.createTokenInfo(loginUser, params);
        System.out.println(tokenInfo);
        Map<String, String> response = new HashMap<>(tokenInfo);
        response.put("access_token", tokenInfo.get("token"));
        // fixed 重复数据
        response.remove("token");
        response.put("token_type", "Bearer");
        String iat = tokenInfo.get("iat");
        String exp = tokenInfo.get("exp");
        if (StringUtils.isNotBlank(iat) && StringUtils.isNotBlank(exp)) {
            try {
                long expiresIn = (Long.parseLong(exp) - Long.parseLong(iat)) / 1000;
                response.put("expires_in", String.valueOf(expiresIn));
            } catch (NumberFormatException ignored) {
                // ignore format errors
            }
        }
        return response;
    }

    private void sendRedirectWithError(HttpServletResponse response, String redirectUri, String error, String state) throws IOException {
        if (StringUtils.isBlank(redirectUri)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
            return;
        }
        String target = appendQuery(redirectUri, "error", error);
        if (StringUtils.isNotBlank(state)) {
            target = appendQuery(target, "state", state);
        }
        response.sendRedirect(target);
    }

    private String appendQuery(String url, String key, String value) {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + key + "=" + encoded;
    }

    private boolean isRedirectUriAllowed(OpenApp openApp, String redirectUri) {
        if (StringUtils.isBlank(redirectUri)) {
            return false;
        }
        String redirectUriList = openApp.getRedirectUriList();
        if (StringUtils.isNotBlank(redirectUriList)) {
            List<String> allowedList = new ArrayList<>();
            for (String item : redirectUriList.split("[,;\\s]+")) {
                if (StringUtils.isNotBlank(item)) {
                    allowedList.add(item.trim());
                }
            }
            if (allowedList.isEmpty()) {
                return false;
            }
            return allowedList.contains(redirectUri);
        }
        return false;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
