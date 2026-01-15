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
import work.soho.open.api.result.OpenErrorCode;
import work.soho.open.biz.component.OpenApiLimitFacotory;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.domain.OpenApiStatDay;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.enums.OpenAppEnums;
import work.soho.open.biz.service.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

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
    private final OpenAppIpWhitelistService openAppIpWhitelistService;
    private final OpenApiCallLogService openApiCallLogService;
    private final OpenApiStatDayService openApiStatDayService;
    private final OpenApiLimitFacotory openApiLimitFacotory;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Around(value = "@annotation(work.soho.open.api.annotation.OpenApi) || @within(work.soho.open.api.annotation.OpenApi)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            String appKey = request.getHeader("app-key");
            String reqTime = request.getHeader("req-time");
            String method = request.getMethod();
            String path = request.getRequestURI();

            if (appKey == null) {
                return R.error(OpenErrorCode.MISSING_THE_NECESSARY_REQUEST);
            }

            // 判断请求方法， 只支持 GET,POST,PUT,DELETE
            if (!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("POST")
                    && !method.equalsIgnoreCase("PUT") && !method.equalsIgnoreCase("DELETE")) {
                return R.error(OpenErrorCode.MISSING_THE_NECESSARY_REQUEST);
            }

            OpenApp openApp = openAppService.getOpenAppByKey(appKey);

            // 检查app是否已经通过审核
            if (openApp.getStatus() != OpenAppEnums.Status.ACTIVE.getId()) {
                return R.error(OpenErrorCode.THE_APP_FAILED_THE_REVIEW);
            }

            if (openApp == null) {
                return R.error(OpenErrorCode.APP_KEY_ERROR);
            }

            // 获取api信息
            work.soho.open.biz.domain.OpenApi api = openApiService.getByMethodAndPathAndVersion(method, path, "1.0.0");

            // 检查接口是否允许访问
            if (!canAccess(openApp, method, path)) {
                return R.error(OpenErrorCode.INSUFFICIENT_INTERFACE_PERMISSIONS);
            }

            // app限速
            if (!openApiLimitFacotory.getHandler(openApp.getId(), openApp.getQpsLimit()).tryAcquire()) {
                return R.error(OpenErrorCode.REQUESTS_EXCEED_THE_MAXIMUM_RATE_OF_THE_APP);
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
