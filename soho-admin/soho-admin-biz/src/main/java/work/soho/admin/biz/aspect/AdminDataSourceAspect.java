package work.soho.admin.biz.aspect;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(-1)
@Log4j2
public class AdminDataSourceAspect {
    @Autowired
    private DynamicDataSourceProperties properties;
    @Around("bean(admin*ServiceImpl) && execution(* com.baomidou.mybatisplus.extension.service.IService+.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.debug("切换数据源：" + getDefaultDataSource());
            DynamicDataSourceContextHolder.push(getDefaultDataSource());
            return joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            log.debug("切换数据源退出:" + DynamicDataSourceContextHolder.peek());
            DynamicDataSourceContextHolder.poll();
        }
    }

    public String getDefaultDataSource() {
        return properties.getPrimary();
    }

}
