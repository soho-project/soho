package work.soho.common.database.aspect;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import work.soho.common.database.annotation.PublishDeleteNotify;
import work.soho.common.database.event.BeforeBatchDeleteEvent;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Aspect
@Component
@Slf4j
public class ServiceDeleteEventAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    public ServiceDeleteEventAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 拦截被 @PublishDeleteEvents 注解的类中的删除方法
     */
    @Around("execution(* com.baomidou.mybatisplus.extension.service.IService+.remove*(..)) " +
            "&& @target(work.soho.common.database.annotation.PublishDeleteNotify)")
    public Object handleDeleteMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String methodName = method.getName();

        // 获取类级别的注解
        PublishDeleteNotify classAnnotation = joinPoint.getTarget().getClass()
                .getAnnotation(PublishDeleteNotify.class);

        if (classAnnotation == null) {
            return joinPoint.proceed();
        }

        // 检查方法是否匹配配置的模式
        if (!isMethodMatched(methodName, classAnnotation.methodPatterns())) {
            return joinPoint.proceed();
        }

//        // 获取方法级别的注解（如果有）
//        PublishDeleteNotify methodAnnotation = method.getAnnotation(PublishDeleteNotify.class);
//
//        // 如果方法级别配置了跳过，则直接执行
//        if (methodAnnotation != null) {
//            return joinPoint.proceed();
//        }

        // 1. 在删除前查询数据
        Object entityToDelete = resolveEntityToDelete(joinPoint, method, classAnnotation);
        Object entityId = resolveEntityId(joinPoint, method, classAnnotation, entityToDelete);

        // 2. 发布删除前事件
        if (entityId != null && entityToDelete != null) {
            publishBeforeDeleteEvent(joinPoint, classAnnotation,
                    methodName, entityId, entityToDelete);
        }

        // 3. 执行删除操作
        Object result = joinPoint.proceed();

        // 4. 记录执行结果
        logDeleteResult(methodName, entityId, result);

        return result;
    }

    /**
     * 检查方法名是否匹配配置的模式
     */
    private boolean isMethodMatched(String methodName, String[] patterns) {
        for (String pattern : patterns) {
            if (matchesPattern(methodName, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 简单的模式匹配（支持 * 通配符）
     */
    private boolean matchesPattern(String methodName, String pattern) {
        if (pattern.equals("*")) return true;
        if (pattern.equals(methodName)) return true;

        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return methodName.startsWith(prefix);
        }

        if (pattern.startsWith("*")) {
            String suffix = pattern.substring(1);
            return methodName.endsWith(suffix);
        }

        return false;
    }

    /**
     * 解析要删除的实体数据
     */
    private Object resolveEntityToDelete(ProceedingJoinPoint joinPoint, Method method,
                                         PublishDeleteNotify classAnnotation) {
        try {
            // 使用默认的实体解析逻辑
            return resolveEntityByMethod(joinPoint, method);

        } catch (Exception e) {
            log.warn("Failed to resolve entity for deletion in method: {}", method.getName(), e);
            return null;
        }
    }

    /**
     * 根据方法类型解析实体
     */
    private Object resolveEntityByMethod(ProceedingJoinPoint joinPoint, Method method) {
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();

        try {
            if ("removeById".equals(methodName) && args.length > 0) {
                // 根据ID删除 - 调用getById查询
                Method getByIdMethod = findMethod(joinPoint.getTarget(), "getById", Serializable.class);
                if (getByIdMethod != null) {
                    return getByIdMethod.invoke(joinPoint.getTarget(), args[0]);
                }
            }
            else if ("removeByIds".equals(methodName) && args.length > 0 && args[0] instanceof Collection) {
                // 批量删除 - 调用listByIds查询
                Method listByIdsMethod = findMethod(joinPoint.getTarget(), "listByIds", Collection.class);
                if (listByIdsMethod != null) {
                    return listByIdsMethod.invoke(joinPoint.getTarget(), args[0]);
                }
            }
            else if ("remove".equals(methodName) && args.length > 0) {
                // 根据条件删除 - 调用list查询
                Method listMethod = findMethod(joinPoint.getTarget(), "list", Wrapper.class);
                if (listMethod != null) {
                    return listMethod.invoke(joinPoint.getTarget(), args[0]);
                }
            }
        } catch (Exception e) {
            log.debug("Failed to resolve entity by method: {}", methodName, e);
        }

        return null;
    }

    /**
     * 解析实体ID
     */
    private Object resolveEntityId(ProceedingJoinPoint joinPoint, Method method,
                                   PublishDeleteNotify classAnnotation,
                                   Object entity) {
        try {
            // 使用默认的ID解析逻辑
            return resolveIdByMethod(joinPoint, method, entity);

        } catch (Exception e) {
            log.warn("Failed to resolve entity ID in method: {}", method.getName(), e);
            return null;
        }
    }

    /**
     * 根据方法类型解析ID
     */
    private Object resolveIdByMethod(ProceedingJoinPoint joinPoint, Method method, Object entity) {
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();

        if ("removeById".equals(methodName) && args.length > 0) {
            return args[0]; // 第一个参数就是ID
        }
        else if ("removeByIds".equals(methodName) && args.length > 0) {
            return args[0]; // 参数是ID集合
        }
        else if (entity != null) {
            // 尝试从实体对象中提取ID
            return extractIdFromEntity(entity);
        }

        return null;
    }

    /**
     * 尝试从实体对象中提取实体类型
     */
    private Class<?> getEntityClass(Class<?> serviceClass) {
        // 获取父类泛型
        Type superClass = serviceClass.getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) superClass).getActualTypeArguments();
            if (typeArgs.length >= 2) {
                // 第二个泛型就是实体类型
                Type entityType = typeArgs[1];
                if (entityType instanceof Class<?>) {
                    return (Class<?>) entityType;
                }
            }
        }
        throw new RuntimeException("无法解析实体类型");
    }

    /**
     * 发布删除前事件
     */
    private void publishBeforeDeleteEvent(ProceedingJoinPoint joinPoint,
                                          PublishDeleteNotify classAnnotation,
                                          String methodName, Object entityId, Object entity) {
        // 确定实体类型
        Class<?> entityType = classAnnotation.entityType();

        if(entityType == void.class) {
            entityType = getEntityClass(joinPoint.getTarget().getClass());
        }

        BeforeBatchDeleteEvent batchEvent = new BeforeBatchDeleteEvent(
                joinPoint.getTarget(),
                entityType.getName(),
                (entityId instanceof Collection ? new ArrayList<>((Collection<?>) entityId) : Collections.singletonList(entityId)),
                (entity instanceof Collection ? new ArrayList<>( (Collection<?>) entity) : new ArrayList<>(Arrays.asList( entity))),
                methodName
        );        // 发布事件
        eventPublisher.publishEvent(batchEvent);
        log.debug("Published delete event for {} in method: {}", entityType, methodName);



//        if (entity instanceof Collection) {
//            // 批量删除事件
//            List<Object> entities = new ArrayList<>((Collection<?>) entity);
//            List<Object> entityIds = entities.stream()
//                    .map(this::extractIdFromEntity)
//                    .collect(Collectors.toList());
//
//            BeforeBatchDeleteEvent batchEvent = new BeforeBatchDeleteEvent(
//                    joinPoint.getTarget(),
//                    entityType.getName(),
//                    entityIds,
//                    entities,
//                    methodName
//            );        // 发布事件
//            eventPublisher.publishEvent(batchEvent);
//            log.debug("Published delete event for {} in method: {}", entityType, methodName);
//
//        } else {
//            // 单个删除事件
//            BeforeDeleteEvent event = new BeforeDeleteEvent(
//                    joinPoint.getTarget(),
//                    entityType.getName(),
//                    entityId,
//                    entity,
//                    methodName
//            );
//            // 发布事件
//            eventPublisher.publishEvent(event);
//            log.debug("Published delete event for {} in method: {}", entityType, methodName);
//        }


    }

    /**
     * 工具方法：查找方法
     */
    private Method findMethod(Object target, String methodName, Class<?>... paramTypes) {
        try {
            return target.getClass().getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 工具方法：计算SpEL表达式
     */
    private Object evaluateExpression(String expression, ProceedingJoinPoint joinPoint, Object result) {
        try {
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable("args", joinPoint.getArgs());
            context.setVariable("result", result);
            context.setVariable("target", joinPoint.getTarget());
            context.setVariable("methodName", joinPoint.getSignature().getName());

            Expression expr = expressionParser.parseExpression(expression);
            return expr.getValue(context);
        } catch (Exception e) {
            log.warn("Failed to evaluate expression: {}", expression, e);
            return null;
        }
    }

    /**
     * 工具方法：从实体中提取ID
     */
    private Object extractIdFromEntity(Object entity) {
        if (entity == null) return null;

        try {
            // 尝试通过反射获取ID字段
            Field idField = null;
            Class<?> clazz = entity.getClass();

            // 查找常见的ID字段名
            for (String fieldName : Arrays.asList("id", "Id", "ID", "entityId")) {
                try {
                    idField = clazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    // 继续尝试下一个字段名
                }
            }

            if (idField != null) {
                idField.setAccessible(true);
                return idField.get(entity);
            }
        } catch (Exception e) {
            log.debug("Failed to extract ID from entity", e);
        }

        return null;
    }

    /**
     * 记录删除结果
     */
    private void logDeleteResult(String methodName, Object entityId, Object result) {
        if (Boolean.TRUE.equals(result) || (result instanceof Integer && (Integer) result > 0)) {
            log.debug("Delete operation {} completed successfully for ID: {}", methodName, entityId);
        } else {
            log.debug("Delete operation {} may have failed or no records affected for ID: {}",
                    methodName, entityId);
        }
    }
}