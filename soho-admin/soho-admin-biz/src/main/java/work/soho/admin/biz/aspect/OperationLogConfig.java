package work.soho.admin.biz.aspect;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.admin.biz.config.AdminSysConfig;
import work.soho.admin.biz.domain.AdminOperationLog;
import work.soho.admin.biz.service.AdminOperationLogService;
import work.soho.common.security.annotation.Node;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.RequestUtil;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Log4j2
@Aspect
@Component
@Configuration
@RequiredArgsConstructor
public class OperationLogConfig {

    private final AdminOperationLogService adminOperationLogService;

    private final AdminSysConfig adminSysConfig;


    @Around(value = "@within(org.springframework.web.bind.annotation.RestController)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {

        Boolean isEnable = adminSysConfig.getAdminOperationLogEnable();
        Set<String> methods = adminSysConfig.getAdminOperationLogMethods();
        //Node annotation
        Node node = getNode(invocation);

        //获取请求参数
        String jsonParams = getJsonParams(invocation);
        AdminOperationLog adminOperationLog = new AdminOperationLog();
        adminOperationLog.setAdminUserId(SecurityUtils.getLoginUserId());
        adminOperationLog.setUpdatedTime(LocalDateTime.now());
        adminOperationLog.setCreatedTime(LocalDateTime.now());
        adminOperationLog.setMethod(RequestUtil.getRequest().getMethod());
        adminOperationLog.setPath(RequestUtil.getRequest().getRequestURI());
        adminOperationLog.setParams(jsonParams);
        if(node != null) {
            adminOperationLog.setContent(node.name());
        }

        try {
            Object result = invocation.proceed();
            if(isEnable) {
                adminOperationLog.setResponse(JSONUtil.toJsonStr(result));
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if(isEnable && methods.contains(adminOperationLog.getMethod())) {
                adminOperationLogService.save(adminOperationLog);
            }
        }
    }

    /**
     * before method
     *
     * @param invocation
     * @return
     */
    public String getJsonParams(ProceedingJoinPoint invocation) {
        Object[] args = invocation.getArgs();
        String[] argNames = ((MethodSignature)invocation.getSignature()).getParameterNames();
        Map<String, Object> paramsMap = new HashMap<>();
        for (int i=0; i<args.length; i++) {
            paramsMap.put(argNames[i], args[i]);
        }
        return JacksonUtils.toJson(paramsMap);
    }

    private Node getNode(ProceedingJoinPoint invocation) {
        String methodName=invocation.getSignature().getName();
        Class<?> classTarget=invocation.getTarget().getClass();
        Class<?>[] par=((MethodSignature) invocation.getSignature()).getParameterTypes();
        try {
            Method objMethod=classTarget.getMethod(methodName, par);
            return objMethod.getAnnotation(Node.class);
        } catch (Exception e) {
            return null;
        }
    }
}
