package work.soho.admin.biz.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import work.soho.admin.api.annotation.Log;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Aspect
@Component
@Configuration
public class LogAspect {

    @Around(value = "@annotation(work.soho.admin.api.annotation.Log)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {
        String requestId = String.valueOf(IDGeneratorUtils.snowflake());
        Log l = getLog(invocation);
        String jsonParams = getJsonParams(invocation);
        try {
            Object result = invocation.proceed();
            log.debug("[{}] [{}] method: [{}] params: [{}] response: [{}]",l.value(), requestId, invocation.getSignature(), jsonParams, JacksonUtils.toJson(result));
            return result;
        } catch (Exception e) {
            log.warn("[{}] [{}] method: [{}] params: [{}] exception: [{}]",l.value(), requestId, invocation.getSignature(), jsonParams, JacksonUtils.toJson(e));
            throw e;
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
        JacksonUtils.toJson(paramsMap);
         return null;
     }

    /**
     * get Log
     *
     * @param invocation
     * @return
     */
    private Log getLog(ProceedingJoinPoint invocation) {
        String methodName=invocation.getSignature().getName();
        Class<?> classTarget=invocation.getTarget().getClass();
        Class<?>[] par=((MethodSignature) invocation.getSignature()).getParameterTypes();
        try {
            Method objMethod=classTarget.getMethod(methodName, par);
            return objMethod.getAnnotation(Log.class);
        } catch (Exception e) {
            return null;
        }
    }
}
