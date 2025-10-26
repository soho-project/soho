package work.soho.common.database.aspect;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import work.soho.common.database.annotation.OnAfterSave;
import work.soho.common.database.annotation.OnBeforeSave;
import work.soho.common.database.annotation.PublishSaveNotify;
import work.soho.common.database.event.SaveEvent;
import work.soho.common.database.handler.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 保存事件发布切面
 */
@Aspect
@Log4j2
@Component
@RequiredArgsConstructor
public class ServiceSaveEventAspect {
    private final ApplicationContext applicationContext;

    /**
     * 切点：拦截 IService 的保存相关方法
     *
     * TODO saveOrUpdate 需要特殊处理  只处理保存部分的数据
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
        log.debug("进入保存事件发布切面+++++++++++++");
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return joinPoint.proceed();
        }

        PublishSaveNotify annotation = joinPoint.getTarget().getClass().getAnnotation(PublishSaveNotify.class);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        // 获取第一个参数（实体或实体集合）
        Object entityParam = args[0];

        // 获取保存前数据库中的数据
        Map<Object, Object> oldEntitiesMap = getOldEntitiesFromDatabase(joinPoint.getTarget(), entityParam, annotation);
        // 发布保存前事件
        publishBeforeSaveEvent(joinPoint.getTarget(), entityParam, annotation, oldEntitiesMap);
        // 执行原方法
        Object result = joinPoint.proceed();
        // 如果需要，可以在这里发布保存后事件
        publishAfterSaveEvent(joinPoint.getTarget(), entityParam, annotation, oldEntitiesMap);
        return result;
    }

    /**
     * 发布保存前事件
     */
    @SuppressWarnings("unchecked")
    private void publishBeforeSaveEvent(Object publisher, Object entityParam, PublishSaveNotify annotation, Map<Object, Object> oldEntiesMap) throws InvocationTargetException, IllegalAccessException {
        SaveEvent event = new SaveEvent(publisher);
        event.setEntityType(annotation.entityType().getName());
        event.setOldEntities(oldEntiesMap);
        if (entityParam instanceof Collection) {
            event.setEntities((List<Object>) entityParam);
        } else {
            event.setEntities(List.of(entityParam));
        }

        // 老数据的条数同传参条数一样 则说明没有新增数据  无须分发
        if(oldEntiesMap.size() == event.getEntities().size()) {
            return;
        }

        List<MethodHandler> handlers = findOnBeforeSaveHandlers( annotation);

        if(annotation.async()) {
            asyncPublishEvent(handlers, event);
        } else {
            publishEvent(handlers, event);
        }
    }

    /**
     * 发布保存后事件（可选）
     */
    @SuppressWarnings("unchecked")
    private void publishAfterSaveEvent(Object publisher, Object entityParam, PublishSaveNotify annotation, Map<Object, Object> oldEntiesMap) throws InvocationTargetException, IllegalAccessException {
        SaveEvent event = new SaveEvent(publisher);
        event.setEntityType(annotation.entityType().getName());
        event.setOldEntities(oldEntiesMap);
        if (entityParam instanceof Collection) {
            event.setEntities((List<Object>) entityParam);
        } else {
            event.setEntities(List.of(entityParam));
        }

        // 老数据的条数同传参条数一样 则说明没有新增数据  无须分发
        if(oldEntiesMap.size() == event.getEntities().size()) {
            return;
        }

        List<MethodHandler> handlers = findOnAfterSaveHandlers(annotation);

        if(annotation.async()) {
            asyncPublishEvent(handlers, event);
        } else {
            publishEvent(handlers, event);
        }
    }

    // 触发消息分发
    public void publishEvent(List<MethodHandler> handlers, SaveEvent event) throws InvocationTargetException, IllegalAccessException {
        for(MethodHandler handler : handlers) {
            Object targetBean = handler.getTargetBean();
            Method method = handler.getMethod();

            // 检查方法参数必须为一个
            if(method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Method " + method.getName() + " must have one parameter.");
            }

            try {
                if(event instanceof SaveEvent) {
                    method.invoke(targetBean, event);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    @Async
    public void asyncPublishEvent(List<MethodHandler> handler, SaveEvent event) throws InvocationTargetException, IllegalAccessException {
        publishEvent( handler, event);
    }

    /**
     * 获取所有 OnAfterSave 注解的方法处理器
     *
     * @return
     */
    private List<MethodHandler> findOnAfterSaveHandlers(PublishSaveNotify publishSaveNotify) {
        List<MethodHandler> handlers = new ArrayList<>();
        Map<String, Object> beans =  applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Service.class);

        for(Object bean : beans.values()) {
            Method[] methods = bean.getClass().getMethods();
            for(Method method : methods) {
                OnAfterSave annotation = AnnotationUtils.findAnnotation(method, OnAfterSave.class);
                if(annotation != null && annotation.entityType().getName().equals(publishSaveNotify.entityType().getName())) {
                    handlers.add(new MethodHandler(bean, method, annotation.order(), annotation.async()));
                }
            }
        }

        // 按照order排序； 升序
        handlers.sort(Comparator.comparingInt(MethodHandler::getOrder));

        return handlers;
    }

    private List<MethodHandler> findOnBeforeSaveHandlers(PublishSaveNotify publishSaveNotify) {
        List<MethodHandler> handlers = new ArrayList<>();
        Map<String, Object> beans =  applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Service.class);


        for(Object bean : beans.values()) {
            Method[] methods = bean.getClass().getMethods();
            for(Method method : methods) {
                OnBeforeSave annotation = AnnotationUtils.findAnnotation(method, OnBeforeSave.class);
                if(annotation != null) {
                    System.out.println(annotation.entityType().getName());
                    System.out.println(publishSaveNotify.entityType().getName());
                }
                if(annotation != null && annotation.entityType().getName().equals(publishSaveNotify.entityType().getName())) {
                    handlers.add(new MethodHandler(bean, method, annotation.order(), annotation.async()));
                }
            }
        }

        // 按照order排序； 升序
        handlers.sort(Comparator.comparingInt(MethodHandler::getOrder));

        return handlers;
    }


    /**
     * 从数据库获取保存前的旧数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<Object, Object> getOldEntitiesFromDatabase(Object service, Object entityParam, PublishSaveNotify annotation) {
        Map<Object, Object> oldEntitiesMap = new HashMap<>();

        // 检查service是否实现了IService接口
        if (!(service instanceof IService)) {
            log.warn("Service {} 没有实现 IService 接口，无法获取旧数据", service.getClass().getName());
            return oldEntitiesMap;
        }

        try {
            // 获取实体ID字段名（假设使用MyBatis-Plus，默认使用"id"字段）
            String idFieldName = "id";

            if(annotation.entityType() != null) {
                TableInfo tableInfo = TableInfoHelper.getTableInfo(annotation.entityType());
//                Object idValue = tableInfo.getPropertyValue(entityParam, tableInfo.getKeyProperty());
                idFieldName = tableInfo.getKeyProperty();
            }

            final String idFieldNameFinal = idFieldName;



            if (entityParam instanceof Collection) {
                // 批量保存的情况
                Collection<?> entities = (Collection<?>) entityParam;
                List<Object> ids = entities.stream()
                        .map(entity -> getEntityId(entity, idFieldNameFinal))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (!ids.isEmpty()) {
                    // 调用service的listByIds方法获取旧数据
                    Method listByIdsMethod = service.getClass().getMethod("listByIds", Collection.class);
                    List oldEntities = (List) listByIdsMethod.invoke(service, ids);

                    // 构建ID到实体的映射
                    for (Object oldEntity : oldEntities) {
                        Object id = getEntityId(oldEntity, idFieldNameFinal);
                        if (id != null) {
                            oldEntitiesMap.put(id, oldEntity);
                        }
                    }
                }
            } else {
                // 单个实体保存的情况
                Object id = getEntityId(entityParam, idFieldNameFinal);
                if (id != null) {
                    // 调用service的getById方法获取旧数据
                    Method getByIdMethod = service.getClass().getMethod("getById", id.getClass());
                    Object oldEntity = getByIdMethod.invoke(service, id);
                    if (oldEntity != null) {
                        oldEntitiesMap.put(id, oldEntity);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取保存前数据失败，将继续执行保存操作", e);
            // 如果获取旧数据失败，不影响主要保存流程，返回空Map
        }

        return oldEntitiesMap;
    }

    /**
     * 通过反射获取实体ID值
     */
    private Object getEntityId(Object entity, String idFieldName) {
        try {
            // 首先尝试通过getId方法获取
            Method getIdMethod = entity.getClass().getMethod("getId");
            return getIdMethod.invoke(entity);
        } catch (NoSuchMethodException e) {
            // 如果不存在getId方法，尝试直接通过字段获取
            try {
                java.lang.reflect.Field idField = entity.getClass().getDeclaredField(idFieldName);
                idField.setAccessible(true);
                return idField.get(entity);
            } catch (Exception ex) {
                log.debug("无法获取实体ID字段值: {}", ex.getMessage());
                return null;
            }
        } catch (Exception e) {
            log.debug("通过getId方法获取ID失败: {}", e.getMessage());
            return null;
        }
    }
}