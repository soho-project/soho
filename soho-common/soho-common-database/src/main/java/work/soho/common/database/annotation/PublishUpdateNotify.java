package work.soho.common.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishUpdateNotify {
    /**
     * 实体类型
     */
    Class<?> entityType() default void.class;

    /**
     * 是否需要旧数据
     */
    boolean needOldData() default false;

    /**
     * 是否异步发布事件
     */
    boolean async() default false;
}
