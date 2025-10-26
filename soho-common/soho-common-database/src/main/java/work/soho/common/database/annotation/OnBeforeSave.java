package work.soho.common.database.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnBeforeSave {
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
