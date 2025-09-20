package work.soho.game.biz.aop;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class GameDataSourceAspect {

    private static final String READ_DB_NAME = "game"; // 读数据源
    private static final String WRITE_DB_NAME = "game";  // 写数据源

    /**
     * 定义切入点：work.soho.game.biz.mapper 包下的所有 Mapper 类的方法
     */
    @Pointcut("execution(* work.soho.game.biz.mapper..*.*(..))")
    public void mapperPointcut() {}

    @Before("mapperPointcut()")
    public void beforeMapperMethod(JoinPoint joinPoint) {
        String dataSource = determineDataSource(joinPoint);
        // 设置当前数据源到上下文
        DynamicDataSourceContextHolder.push(dataSource);
    }

    @After("mapperPointcut()")
    public void afterMapperMethod(JoinPoint joinPoint) {
        // 清理数据源上下文
        DynamicDataSourceContextHolder.poll();
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