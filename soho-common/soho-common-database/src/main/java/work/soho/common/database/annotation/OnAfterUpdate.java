package work.soho.common.database.annotation;

public @interface OnAfterUpdate {
    /**
     * 实体类型
     */
    Class<?> entityType() default void.class;

    /**
     * 是否启用异步事件处理
     */
    boolean async() default true;
}
