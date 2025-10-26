package work.soho.common.database.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnAfterUpdate {
    /**
     * 实体类型
     */
    Class<?> entityType() default void.class;

    /**
     * 删除顺序（数值越小越先执行）
     */
    int order() default 0;

    /**
     * 是否启用异步事件处理
     */
    boolean async() default true;
}
