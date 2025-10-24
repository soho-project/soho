package work.soho.common.database.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import work.soho.common.database.event.BeforeBatchDeleteEvent;
import work.soho.common.database.annotation.OnBeforeDelete;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 批量删除事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchDeleteEventListener {

    private final ApplicationContext applicationContext;
    private final Map<String, List<MethodHandler>> handlerCache = new ConcurrentHashMap<>();

    /**
     * 处理批量删除前事件
     */
    @EventListener
    public void handleBeforeBatchDelete(BeforeBatchDeleteEvent event) {
        String entityType = event.getEntityType();
        List<Object> entityIds = event.getEntityIds();
        List<Object> entities = event.getEntities();

        log.info("开始处理批量删除前事件，实体类型: {}, 实体ID数量: {}", entityType, entityIds.size());

        // 获取所有相关的处理方法
        List<MethodHandler> handlers = findHandlersForEntity(entityType);

        // 按order排序
        handlers.sort(Comparator.comparingInt(MethodHandler::getOrder));

        // 执行处理方法
        for (MethodHandler handler : handlers) {
            try {
                if (handler.isAsync()) {
                    // 异步执行
                    executeAsync(handler, entityIds, entities, event);
                } else {
                    // 同步执行
                    executeSync(handler, entityIds, entities, event);
                }
            } catch (Exception e) {
                log.error("执行关联数据删除方法失败: {}", handler.getMethod().getName(), e);
                // 根据业务需求决定是否抛出异常
                // throw new RuntimeException("关联数据删除失败", e);
            }
        }

        log.info("批量删除前事件处理完成，实体类型: {}", entityType);
    }

    /**
     * 同步执行
     */
    private void executeSync(MethodHandler handler, List<Object> entityIds,
                             List<Object> entities, BeforeBatchDeleteEvent event) {
        Object targetBean = handler.getTargetBean();
        Method method = handler.getMethod();

        try {
            // 根据方法参数类型调用
            if (method.getParameterCount() == 1) {
                if (method.getParameterTypes()[0].equals(List.class)) {
                    method.invoke(targetBean, entityIds);
                } else if (method.getParameterTypes()[0].equals(BeforeBatchDeleteEvent.class)) {
                    method.invoke(targetBean, event);
                }
            } else if (method.getParameterCount() == 2) {
                method.invoke(targetBean, entityIds, entities);
            } else {
                method.invoke(targetBean);
            }

            log.debug("同步执行关联删除方法成功: {}.{}",
                    targetBean.getClass().getSimpleName(), method.getName());
        } catch (Exception e) {
            log.error("同步执行关联删除方法失败: {}.{}",
                    targetBean.getClass().getSimpleName(), method.getName(), e);
            throw new RuntimeException("关联数据删除失败", e);
        }
    }

    /**
     * 异步执行
     */
    @Async
    public void executeAsync(MethodHandler handler, List<Object> entityIds,
                             List<Object> entities, BeforeBatchDeleteEvent event) {
        executeSync(handler, entityIds, entities, event);
    }

    /**
     * 查找实体对应的处理方法
     */
    private List<MethodHandler> findHandlersForEntity(String entityType) {
        return handlerCache.computeIfAbsent(entityType, this::scanHandlers);
    }

    /**
     * 扫描所有处理方法
     */
    private List<MethodHandler> scanHandlers(String entityType) {
        List<MethodHandler> handlers = new ArrayList<>();

        // 获取所有Spring Bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Service.class);

        for (Object bean : beans.values()) {
            // 先检查类上面是否有注解
            // 检查bean类上是否有OnBeforeBatchDelete注解
            OnBeforeDelete classAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), OnBeforeDelete.class);

            // 如果bean类上没有OnBeforeBatchDelete注解，则跳过该bean
//            if (classAnnotation == null) {
//                // todo  调用
//                if(bean instanceof com.baomidou.mybatisplus.extension.service.impl.ServiceImpl) {
//                    com.baomidou.mybatisplus.extension.service.impl.ServiceImpl service = (com.baomidou.mybatisplus.extension.service.impl.ServiceImpl) bean;
//                    System.out.println(service);
//                }
//
//                continue;
//            }

            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                OnBeforeDelete annotation = AnnotationUtils.findAnnotation(method, OnBeforeDelete.class);
                if (annotation != null && annotation.entityType().getName().equals(entityType)) {
                    handlers.add(new MethodHandler(bean, method, annotation.order(), annotation.async()));
                    log.info("注册关联删除处理器: {}.{} -> {}",
                            bean.getClass().getSimpleName(), method.getName(), entityType);
                }
            }
        }

        return handlers;
    }

    /**
     * 方法处理器
     */
    @lombok.Data
    private static class MethodHandler {
        private final Object targetBean;
        private final Method method;
        private final int order;
        private final boolean async;
    }
}