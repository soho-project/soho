package work.soho.common.database.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import work.soho.common.database.event.AfterSaveEvent;
import work.soho.common.database.event.BeforeSaveEvent;

import java.util.Collection;

/**
 * 保存事件发布切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ServiceSaveEventAspect {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 切点：拦截 IService 的保存相关方法
     */
    @Pointcut("(execution(* com.baomidou.mybatisplus.extension.service.IService+.save*(..)) || " +
            "execution(* com.baomidou.mybatisplus.extension.service.IService+.saveOrUpdate*(..)))" +
            " && @target(work.soho.common.database.annotation.PublishSaveNotify)")
    public void saveMethods() {}

    /**
     * 环绕通知：在保存方法执行前后发布事件
     */
    @Around("saveMethods()")
    public Object publishSaveEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return joinPoint.proceed();
        }

        // 获取第一个参数（实体或实体集合）
        Object entityParam = args[0];

        // 检查是否需要发布事件
        if (shouldPublishEvent(entityParam)) {
            // 发布保存前事件
            publishBeforeSaveEvent(joinPoint.getTarget(), entityParam);

            // 执行原方法
            Object result = joinPoint.proceed();

            // 如果需要，可以在这里发布保存后事件
            publishAfterSaveEvent(joinPoint.getTarget(), entityParam, result);

            return result;
        }

        return joinPoint.proceed();
    }

    /**
     * 判断是否需要发布事件
     */
    private boolean shouldPublishEvent(Object entityParam) {
        if (entityParam == null) {
            return false;
        }

        if (entityParam instanceof Collection) {
            Collection<?> collection = (Collection<?>) entityParam;
            if (collection.isEmpty()) {
                return false;
            }
            // 检查集合中第一个元素的类是否有注解
//            Object firstEntity = collection.iterator().next();
//            return firstEntity != null &&
//                    AnnotationUtils.findAnnotation(firstEntity.getClass(), PublishSaveNotify.class) != null;
            return true;
        } else {
            // 检查单个实体类是否有注解
//            return AnnotationUtils.findAnnotation(entityParam.getClass(), PublishSaveNotify.class) != null;
            return true;
        }
    }

    /**
     * 发布保存前事件
     */
    @SuppressWarnings("unchecked")
    private void publishBeforeSaveEvent(Object publisher, Object entityParam) {
        if (entityParam instanceof Collection) {
            BeforeSaveEvent<Collection<Object>> event = new BeforeSaveEvent<>(publisher, (Collection<Object>) entityParam);
            eventPublisher.publishEvent(event);
        } else {
            BeforeSaveEvent<Object> event = new BeforeSaveEvent<>(publisher, entityParam);
            eventPublisher.publishEvent(event);
        }
    }

    /**
     * 发布保存后事件（可选）
     */
    @SuppressWarnings("unchecked")
    private void publishAfterSaveEvent(Object publisher, Object entityParam, Object result) {
        if (entityParam instanceof Collection) {
            AfterSaveEvent<Collection<Object>> event = new AfterSaveEvent<>(publisher, (Collection<Object>) entityParam);
            eventPublisher.publishEvent(event);
        } else {
            AfterSaveEvent<Object> event = new AfterSaveEvent<>(publisher, entityParam);
            eventPublisher.publishEvent(event);
        }
    }
}