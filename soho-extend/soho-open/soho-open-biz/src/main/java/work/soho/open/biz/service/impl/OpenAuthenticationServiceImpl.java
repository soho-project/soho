package work.soho.open.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.util.ContentCachingRequestWrapper;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.component.BodyCachingHttpServletRequestWrapper;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.enums.OpenAppEnums;
import work.soho.open.biz.service.OpenAppService;
import work.soho.open.biz.service.OpenAuthenticationService;
import work.soho.open.biz.service.OpenUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.*;

import static cn.hutool.crypto.SecureUtil.md5;

@Log4j2
@Service
@RequiredArgsConstructor
public class OpenAuthenticationServiceImpl implements OpenAuthenticationService {
    private final OpenAppService openAppService;
    private final OpenUserService openUserService;

    /**
     * 开放平台鉴权实现
     *
     * @param request
     * @return
     */
    @Override
    public SohoUserDetails getLoginUser(HttpServletRequest request) {
        try {
            String appKey = request.getHeader("app-key");
            String sign = request.getHeader("sign");
            String reqTime = request.getHeader("req-time");
            String method = request.getMethod();
            String path = request.getRequestURI();

            if (appKey == null || sign == null || reqTime == null) {
                throw new InvalidParameterException("缺少必要请求头");
            }

            // 判断请求方法， 只支持 GET,POST,PUT,DELETE
            if (!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("POST")
                    && !method.equalsIgnoreCase("PUT") && !method.equalsIgnoreCase("DELETE")) {
                throw new InvalidParameterException("不支持的请求方法");
            }

            OpenApp openApp = openAppService.getOpenAppByKey(appKey);

            // 检查app是否已经通过审核
            if (openApp.getStatus() != OpenAppEnums.Status.ACTIVE.getId()) {
                throw new InvalidParameterException("app未通过审核");
            }

            if (openApp == null) {
                throw new InvalidParameterException("无效 app-key");
            }
            String appSecret = openApp.getAppSecret();

            if (appSecret == null) {
                throw new InvalidParameterException("无效 app-/");
            }

            // 检查签名
            if(!checkKey(request, openApp, sign, appKey, reqTime, appSecret, method, path)) {
                throw new InvalidParameterException("签名校验失败");
            }

            Assert.notNull(openApp.getUserId(), "应用未审核开放 || 应用账号异常");

            // 组装用户信息
            SohoUserDetails user = new SohoUserDetails();
            user.setId(openApp.getId());
            user.setUsername(openApp.getAppName());

            // 扩展参数
            HashMap<String, Object> params = new HashMap<>();
            params.put("appId", openApp.getId());
            params.put("appKey", openApp.getAppKey());
            user.setClaims( params);

            user.setAuthorities(AuthorityUtils.createAuthorityList("openApp"));
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean checkKey(HttpServletRequest request, OpenApp openApp, String sign, String appKey, String reqTime,
                             String appSecret, String method, String path) {
        try {
            // 2. 计算服务端签名
            String serverSign = getSignByRequest(request, appSecret);
            log.info("sign: " + sign);
            log.info("serverSign: " + serverSign);
            log.info("reqTime: " + reqTime);

            // 3. 对比签名
            if (!serverSign.equalsIgnoreCase(sign)) {
                throw new InvalidParameterException("签名校验失败");
            }

            // 4. （可选）校验时间戳
            if (!checkReqTime(reqTime)) {
                throw new InvalidParameterException("请求已过期");
            }

            return true;
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkReqTime(String reqTime) {
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();

        // 计算时间差（毫秒）
        long timeDiff = currentTime - Long.parseLong(reqTime) * 1000;

        // 设置允许的时差范围（毫秒）
        long allowedTimeDiff = 5 * 60 * 1000; // 5 分钟

        // 检查时间差是否在允许范围内
        return timeDiff <= allowedTimeDiff;
    }


    private String getSignByRequest(HttpServletRequest request, String appSecret) throws Exception {
        String method = request.getMethod();
        String body = "";

        if(method.equalsIgnoreCase("GET")) {
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    // 只取第一个值
                    linkedHashMap.put(key, values[0]);
                } else {
                    linkedHashMap.put(key, null);
                }
            }
            body = buildQueryString(linkedHashMap);
        } else {
            BodyCachingHttpServletRequestWrapper bodyWrapper = unwrapBodyCachingWrapper(request);
            if (bodyWrapper != null) {
                body = bodyWrapper.getBody();
            } else if (request instanceof ContentCachingRequestWrapper) {
                byte[] cached = ((ContentCachingRequestWrapper) request).getContentAsByteArray();
                body = new String(cached, StandardCharsets.UTF_8);
            } else {
                // No cached body available; keep empty to avoid ClassCastException
                body = "";
            }
        }

        log.info("sign body:" + body + "_" + appSecret);
        String sign = md5(body + "_" + appSecret);
        return sign;
    }

    private BodyCachingHttpServletRequestWrapper unwrapBodyCachingWrapper(HttpServletRequest request) {
        HttpServletRequest current = request;
        while (current instanceof HttpServletRequestWrapper) {
            if (current instanceof BodyCachingHttpServletRequestWrapper) {
                return (BodyCachingHttpServletRequestWrapper) current;
            }
            current = (HttpServletRequest) ((HttpServletRequestWrapper) current).getRequest();
        }
        return null;
    }

    private static String buildQueryString(Object obj) throws Exception {
        Map<String, Object> map;
        map = (Map<String, Object>) obj;

        // 对 Map 的键进行排序
        List<String> sortedKeys = new ArrayList<>(map.keySet());
        Collections.sort(sortedKeys); // 或使用 sortedKeys.sort(Comparator.naturalOrder())

        StringBuilder sb = new StringBuilder();
        for (String key : sortedKeys) {
            Object value = map.get(key);
            if (value != null) {
                sb.append(key)
                        .append("=")
                        .append(java.net.URLEncoder.encode(value.toString(), "UTF-8"))
                        .append("&");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // 去掉最后一个 &
        }
        return sb.toString();
    }
}
