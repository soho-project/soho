package work.soho.common.excel.annotation;

import work.soho.common.excel.listener.ExcelReadListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * ExcelImport
 * </p>
 *
 * @author livk
 * @date 2022/1/22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

	Class<? extends ExcelReadListener<?>> parse();

	String fileName();

}
