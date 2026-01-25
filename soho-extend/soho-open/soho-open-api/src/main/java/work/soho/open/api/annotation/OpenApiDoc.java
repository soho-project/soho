package work.soho.open.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenApiDoc {
    String value() default "";
    String name() default "";
    String version() default "1.0.0";
    boolean needAuth() default true;
    /**
     * 接口鉴权角色（openUser/openApp）
     */
    String authRole() default "openApp";
    String description() default "";
}
