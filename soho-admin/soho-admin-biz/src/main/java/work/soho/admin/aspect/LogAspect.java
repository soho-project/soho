package work.soho.admin.aspect;

import com.littlenb.snowflake.sequence.IdGenerator;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import work.soho.admin.annotation.Log;
import work.soho.common.core.util.JacksonUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Aspect
@Component
@Configuration
public class LogAspect {
    @Autowired
    private IdGenerator generator;

    @Around(value = "@annotation(work.soho.admin.annotation.Log)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {
        String requestId = String.valueOf(generator.nextId());
        Log l = getLog(invocation);
        try {
            beforeMethod(l, invocation, requestId);
            Object result = invocation.proceed();
            log.debug("[{}] [{}] method: [{}] response: [{}]",l.value(), requestId, invocation.getSignature(), JacksonUtils.toJson(result));
            return result;
        } catch (Exception e) {
            log.warn("[{}] [{}] method: [{}] response: [{}]",l.value(), requestId, invocation.getSignature(), JacksonUtils.toJson(e));
            throw e;
        }
    }

    /**
     * before method
     *
     * @param l
     * @param invocation
     * @param uuid
     */
     public void beforeMethod(Log l, ProceedingJoinPoint invocation, String uuid) {
        Object[] args = invocation.getArgs();
        String[] argNames = ((MethodSignature)invocation.getSignature()).getParameterNames();
        Map<String, Object> paramsMap = new HashMap<>();
        for (int i=0; i<args.length; i++) {
            paramsMap.put(argNames[i], args[i]);
        }
        log.debug("[{}] [{}] method: [{}] params: [{}]",l.value(), uuid, invocation.getSignature(), JacksonUtils.toJson(paramsMap));
    }

    /**
     * get Log
     *
     * @param invocation
     * @return
     */
    private Log getLog(ProceedingJoinPoint invocation) {
        String methodName=invocation.getSignature().getName();
        Object[] args=invocation.getArgs();
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
