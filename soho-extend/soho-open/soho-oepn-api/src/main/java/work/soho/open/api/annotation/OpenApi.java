package work.soho.open.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenApi {
    String value();
    String name() default "";
    String version() default "1.0.0";
    boolean needAuth() default true;
    String description() default "";
}
