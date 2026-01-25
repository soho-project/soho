package work.soho.open.api.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@OpenApiDoc
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenApi {
    @AliasFor(annotation = OpenApiDoc.class, attribute = "value")
    String value();

    @AliasFor(annotation = OpenApiDoc.class, attribute = "name")
    String name() default "";

    @AliasFor(annotation = OpenApiDoc.class, attribute = "version")
    String version() default "1.0.0";

    @AliasFor(annotation = OpenApiDoc.class, attribute = "needAuth")
    boolean needAuth() default true;

    @AliasFor(annotation = OpenApiDoc.class, attribute = "authRole")
    String authRole() default "";

    @AliasFor(annotation = OpenApiDoc.class, attribute = "description")
    String description() default "";
}
