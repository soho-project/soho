package work.soho.common.data.excel.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import work.soho.common.data.excel.view.DefaultExcelView;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {
    @AliasFor("fileName")
    String value() default "";
    String fileName() default "";
    Class<?> modelClass();
    Class<?> excelView() default DefaultExcelView.class;
}
