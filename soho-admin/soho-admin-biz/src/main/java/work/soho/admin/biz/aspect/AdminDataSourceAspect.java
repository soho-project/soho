package work.soho.admin.biz.aspect;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(-1)
public class AdminDataSourceAspect {
    @Autowired
    private DynamicDataSourceProperties properties;
    @Around("execution(* work.soho.admin.biz.mapper.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            DynamicDataSourceContextHolder.push(getDefaultDataSource());
            return joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    public String getDefaultDataSource() {
        return properties.getPrimary();
    }
}
