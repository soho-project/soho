package work.soho.common.data.excel.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.data.excel.model.ExcelModel;
import work.soho.common.data.excel.view.DefaultExcelView;
import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
public class ExcelExportAspect {
    @Around(value = "@annotation(work.soho.common.data.excel.annotation.ExcelExport)")
    public ModelAndView afterReturning(ProceedingJoinPoint joinPoint) throws Throwable {
        Object obj = joinPoint.proceed();
        if(!(obj instanceof List) && !(obj instanceof ExcelModel)) {
            throw new RuntimeException("返回值必须为 List 或者 ExcelModel");
        }

        ExcelModel excelModel = null;
        if(obj instanceof List) {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            ExcelExport excelExport = method.getAnnotation(ExcelExport.class);
            Class<?> modelClass = excelExport.modelClass();

            excelModel = new ExcelModel();
            excelModel.setFileName("test.xls").addSheet((List<?>) obj, modelClass);
        } else if(obj instanceof ExcelModel) {
            //nothing
        }

        return new ModelAndView(new DefaultExcelView(), excelModel);
    }
}
