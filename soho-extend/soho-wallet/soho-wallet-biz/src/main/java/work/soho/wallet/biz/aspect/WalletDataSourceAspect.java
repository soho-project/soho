package work.soho.wallet.biz.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Integer.MIN_VALUE)
@Log4j2
public class WalletDataSourceAspect {

    private static final String READ_DB_NAME = "wallet"; // 读数据源
    private static final String WRITE_DB_NAME = "wallet";  // 写数据源

    @Around("bean(wallet*ServiceImpl) && execution(* com.baomidou.mybatisplus.extension.service.IService+.*(..))")
    public Object dbAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("切换数据源：" + determineDataSource(joinPoint));
            DynamicDataSourceContextHolder.push(determineDataSource(joinPoint));
            return joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("切换数据源退出:" + DynamicDataSourceContextHolder.peek());
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 根据方法名判断使用读库还是写库
     */
    private String determineDataSource(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println(methodName);
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