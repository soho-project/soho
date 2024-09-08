package work.soho.api.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源节点注解
 *
 * 标注了该注解的action则会进行精确的权限检查
 * 默认用户ID为1的用户则不进行权限检查
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Node {
    String value();
    String name() default "";
}
