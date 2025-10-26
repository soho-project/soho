package work.soho.common.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishSaveNotify {
    /**
     * 实体类型
     */
    Class<?> entityType() default void.class;

    /**
     * 是否启用异步事件处理
     */
    boolean async() default true;
}
