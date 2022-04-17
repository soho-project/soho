package work.soho.common.data.excel.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注导入导出模型字段标题
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
    @AliasFor("title")
    String value();

    String title() default "";

    String dateFormat() default "yyyy-dd-MM";
}
