package work.soho.shop.biz.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(-1)
public class ShopDataSourceAspect {

    private static final String READ_DB_NAME = "shop"; // 读数据源
    private static final String WRITE_DB_NAME = "shop";  // 写数据源

    @Around("execution(* work.soho.shop.biz.mapper..*(..)) || execution(* work.soho.shop.biz.service..*(..))")
    public Object dbAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            DynamicDataSourceContextHolder.push(determineDataSource(joinPoint));
            return joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 根据方法名判断使用读库还是写库
     */
    private String determineDataSource(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        // 读操作的方法前缀
        if (methodName.startsWith("get") ||
                methodName.startsWith("select") ||
                methodName.startsWith("find") ||
                methodName.startsWith("query") ||
                methodName.startsWith("count") ||
                methodName.startsWith("list") ||
                methodName.startsWith("search")) {
            return READ_DB_NAME;
        } else {
            // 写操作：insert、update、delete、save 等
            return WRITE_DB_NAME;
        }
    }
}