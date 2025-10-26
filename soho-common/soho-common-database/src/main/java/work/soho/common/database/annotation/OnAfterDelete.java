package work.soho.common.database.annotation;

import java.lang.annotation.*;

/**
 * 批量删除关联数据注解
 * 标记在Service方法上，当收到BeforeBatchDeleteEvent事件时执行关联数据删除
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnAfterDelete {

    /**
     * 关联的实体类型
     */
    Class<?> entityType();

    /**
     * 删除顺序（数值越小越先执行）
     */
    int order() default 0;

    /**
     * 是否异步执行
     */
    boolean async() default false;
}