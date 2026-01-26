package work.soho.open.biz.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IpUtils;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.api.result.OpenErrorCode;
import work.soho.open.biz.component.OpenApiLimitFacotory;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.domain.OpenApiStatDay;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.enums.OpenAppEnums;
import work.soho.open.biz.service.OpenApiCallLogService;
import work.soho.open.biz.service.OpenApiService;
import work.soho.open.biz.service.OpenApiStatDayService;
import work.soho.open.biz.service.OpenAppApiService;
import work.soho.open.biz.service.OpenAppService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


/**
 * 这里不做验签，
 *
 * 这里做限流，计数，访问权限灯验证
 */
@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class OpenApiAspect {
    private final OpenApiService openApiService;
    private final OpenAppService openAppService;
    private final OpenAppApiService openAppApiService;
    private final OpenApiCallLogService openApiCallLogService;
    private final OpenApiStatDayService openApiStatDayService;
    private final OpenApiLimitFacotory openApiLimitFacotory;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String API_VERSION = "1.0.0";
    private static final Set<String> ALLOWED_METHODS = Set.of("GET", "POST", "PUT", "DELETE");

    @Around(value = "@annotation(work.soho.open.api.annotation.OpenApi) || @within(work.soho.open.api.annotation.OpenApi)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return R.error(OpenErrorCode.MISSING_THE_NECESSARY_REQUEST);
            }
            HttpServletRequest request = attrs.getRequest();
            String method = request.getMethod();
            String path = request.getRequestURI();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SohoUserDetails user = (auth != null && auth.getPrincipal() instanceof SohoUserDetails)
                    ? (SohoUserDetails) auth.getPrincipal()
                    : null;

            if (user == null) {
                return R.error(OpenErrorCode.USER_NOT_LOGGED_IN);
            }

            String appKey = (String) user.getClaims().get("appKey");


            if (appKey == null) {
                return R.error(OpenErrorCode.MISSING_THE_NECESSARY_REQUEST);
            }

            // 判断请求方法， 只支持 GET,POST,PUT,DELETE
            if (!isAllowedMethod(method)) {
                return R.error(OpenErrorCode.MISSING_THE_NECESSARY_REQUEST);
            }

            OpenApp openApp = openAppService.getOpenAppByKey(appKey);
            if (openApp == null) {
                return R.error(OpenErrorCode.APP_KEY_ERROR);
            }

            // 检查app是否已经通过审核
            if (openApp.getStatus() != OpenAppEnums.Status.ACTIVE.getId()) {
                return R.error(OpenErrorCode.THE_APP_FAILED_THE_REVIEW);
            }

            // 获取api信息
            work.soho.open.biz.domain.OpenApi api = resolveOpenApi(method, path);

            // 检查接口是否允许访问
            if (!canAccess(openApp, api)) {
                return R.error(OpenErrorCode.INSUFFICIENT_INTERFACE_PERMISSIONS);
            }

            // app限速
            if (!openApiLimitFacotory.getHandler(openApp.getId(), openApp.getQpsLimit()).tryAcquire()) {
                return R.error(OpenErrorCode.REQUESTS_EXCEED_THE_MAXIMUM_RATE_OF_THE_APP);
            }

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
                LocalDate statDate = openApiCallLog.getCreatedTime() != null
                        ? openApiCallLog.getCreatedTime().toLocalDate()
                        : LocalDate.now();
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

    private boolean canAccess(OpenApp openApp, String method, String path) {
        return canAccess(openApp, resolveOpenApi(method, path));
    }

    private boolean canAccess(OpenApp openApp, work.soho.open.biz.domain.OpenApi openApi) {
        if (openApp == null || openApi == null) {
            return false;
        }
        // 检查当前访问的appKey 是否有权限访问该接口
        if(!openAppApiService.canAccess(openApp.getId(), openApi.getId())) {
            return false;
        }
        return true;
    }

    private work.soho.open.biz.domain.OpenApi resolveOpenApi(String method, String path) {
        // 精准匹配
        work.soho.open.biz.domain.OpenApi openApi =
                openApiService.getByMethodAndPathAndVersion(method, path, API_VERSION);
        if (openApi != null) {
            return openApi;
        }
        // 模糊spring风格路径匹配
        List<work.soho.open.biz.domain.OpenApi> openApis =
                openApiService.list(new LambdaQueryWrapper<work.soho.open.biz.domain.OpenApi>()
                        .eq(work.soho.open.biz.domain.OpenApi::getMethod, method)
                        .eq(work.soho.open.biz.domain.OpenApi::getVersion, API_VERSION)
                );

        for (work.soho.open.biz.domain.OpenApi api : openApis) {
            // 尝试ant风格匹配
            if (pathMatcher.match(api.getPath(), path)) {
                return api;
            }
        }
        return null;
    }

    private boolean isAllowedMethod(String method) {
        return method != null && ALLOWED_METHODS.contains(method.toUpperCase());
    }
}
