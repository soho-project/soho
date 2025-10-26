package work.soho.shop.biz.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import work.soho.common.core.util.IDGeneratorUtils;

@Aspect
@Component
@Order(-1000)
@Log4j2
public class ShopTestAspect {

    @Around("bean(shop*ServiceImpl)")
    public Object test(ProceedingJoinPoint joinPoint) throws Throwable {

        String id = IDGeneratorUtils.snowflake().toString();
        id = "shop-" + id + "=====================";
        log.debug(id + joinPoint.getSignature().getName());

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            log.debug(id + joinPoint.getSignature().getName());
        }



    }
}
