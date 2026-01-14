package work.soho.open.biz.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IpUtils;
import work.soho.open.api.annotation.OpenApi;
import work.soho.open.biz.component.BodyCachingHttpServletRequestWrapper;
import work.soho.open.biz.component.OpenApiLimitFacotory;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.domain.OpenApiStatDay;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.domain.OpenAppIpWhitelist;
import work.soho.open.biz.enums.OpenAppEnums;
import work.soho.open.biz.service.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.*;

import static cn.hutool.crypto.SecureUtil.md5;


@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class OpenApiAspect {
    private final OpenApiService openApiService;
    private final OpenAppService openAppService;
    private final OpenAppApiService openAppApiService;
    private final OpenAppIpWhitelistService openAppIpWhitelistService;
    private final OpenApiCallLogService openApiCallLogService;
    private final OpenApiStatDayService openApiStatDayService;
    private final OpenApiLimitFacotory openApiLimitFacotory;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Around(value = "@annotation(work.soho.open.api.annotation.OpenApi)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
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

            // 获取api信息
            work.soho.open.biz.domain.OpenApi api = openApiService.getByMethodAndPathAndVersion(method, path, "1.0.0");

            // 检查签名
            if(!checkKey(request, openApp, sign, appKey, reqTime, appSecret, method, path)) {
                return R.error(2401, "鉴权失败");
            }

            // 检查接口是否允许访问
            if (!canAccess(openApp, method, path)) {
                throw new InvalidParameterException("接口权限不足");
            }

            // app限速
            if (!openApiLimitFacotory.getHandler(openApp.getId(), openApp.getQpsLimit()).tryAcquire()) {
                return R.error(2402, "请求超过最大速率 " + openApp.getQpsLimit() + "次每秒" );
            }

            OpenApi openApi = getOpenApi(invocation);
            log.info("openApi: " + openApi);
            // 检查接口是否有权限
            Long startTs = System.currentTimeMillis();
            Object result = invocation.proceed();
            Long endTs = System.currentTimeMillis();
            try {
                // 保存调用日志
                OpenApiCallLog openApiCallLog = new OpenApiCallLog();
                openApiCallLog.setAppId(openApp.getId());
                openApiCallLog.setApiId(api.getId());
                openApiCallLog.setRequestId(request.getRequestURI());
                openApiCallLog.setClientIp(IpUtils.getClientIp());
                openApiCallLog.setResponseCode(200);
                openApiCallLog.setCostMs(endTs - startTs);
                openApiCallLogService.save(openApiCallLog);

                // 日调用统计
                LocalDate statDate = openApiCallLog.getCreatedTime().toLocalDate();
                // 1. 先尝试 update
                boolean updated = openApiStatDayService.update(
                        new UpdateWrapper<OpenApiStatDay>()
                                .eq("app_id", openApp.getId())
                                .eq("api_id", api.getId())
                                .eq("stat_date", statDate)
                                .setSql("call_count = call_count + 1")
                );

                if (!updated) {
                    // 2. 不存在，尝试插入
                    OpenApiStatDay stat = new OpenApiStatDay();
                    stat.setAppId(openApp.getId());
                    stat.setApiId(api.getId());
                    stat.setStatDate(statDate);
                    stat.setCallCount(1);
                    stat.setFailCount(0);

                    try {
                        openApiStatDayService.save(stat);
                    } catch (DuplicateKeyException e) {
                        // 3. 并发下已被插入，再 update 一次
                        openApiStatDayService.update(
                                new UpdateWrapper<OpenApiStatDay>()
                                        .eq("app_id", openApp.getId())
                                        .eq("api_id", api.getId())
                                        .eq("stat_date", statDate)
                                        .setSql("call_count = call_count + 1")
                        );
                    }
                }


            } catch (Exception e) {
                // ignore
                log.error(e);
            }
            return result;
        } catch (Exception e) {
            return R.error(2401, e.getMessage());
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

            // 6. 验证 IP 白名单
            String clientIp = IpUtils.getClientIp();
            List<OpenAppIpWhitelist> openAppIpWhitelists = openAppIpWhitelistService.getByAppId(openApp.getId());
            boolean isInWhitelist = false;
            for (OpenAppIpWhitelist openAppIpWhitelist : openAppIpWhitelists) {
                if (IpUtils.inSubnet(openAppIpWhitelist.getIp(), clientIp)) {
                    isInWhitelist = true;
                }
            }
            if(!isInWhitelist) {
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
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
            body = ((BodyCachingHttpServletRequestWrapper)request).getBody();
        }

        log.info("sign body:" + body + "_" + appSecret);
        String sign = md5(body + "_" + appSecret);
        return sign;
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

    private boolean canAccess(OpenApp openApp, String method, String path) {
        if (openApp == null) {
            return false;
        }
        // 精准匹配
        work.soho.open.biz.domain.OpenApi openApi = openApiService.getByMethodAndPathAndVersion(method, path, "1.0.0");
        if(openApi == null) {
            // 模糊spring风格路径匹配
            List<work.soho.open.biz.domain.OpenApi> openApis = openApiService.list(new LambdaQueryWrapper<work.soho.open.biz.domain.OpenApi>()
                    .eq(work.soho.open.biz.domain.OpenApi::getMethod, method)
                    .eq(work.soho.open.biz.domain.OpenApi::getVersion, "1.0.0")
            );

            for (work.soho.open.biz.domain.OpenApi api : openApis) {
                // 尝试ant风格匹配
                if(pathMatcher.match(api.getPath(), path)) {
                    openApi = api;
                    break;
                }
            }
        }

        if(openApi == null) {
            return false;
        }

        // 检查当前访问的appKey 是否有权限访问该接口
        if(!openAppApiService.canAccess(openApp.getId(), openApi.getId())) {
            return false;
        }
        return true;
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

    private OpenApi getOpenApi(ProceedingJoinPoint invocation) {
        String methodName=invocation.getSignature().getName();
        Class<?> classTarget=invocation.getTarget().getClass();
        Class<?>[] par=((MethodSignature) invocation.getSignature()).getParameterTypes();
        try {
            Method objMethod=classTarget.getMethod(methodName, par);
            return objMethod.getAnnotation(OpenApi.class);
        } catch (Exception e) {
            return null;
        }
    }


}
