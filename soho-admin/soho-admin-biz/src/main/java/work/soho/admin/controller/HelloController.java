package work.soho.admin.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import work.soho.admin.domain.Hello;
import work.soho.admin.service.HelloService;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.captcha.utils.CaptchaUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.data.excel.model.ExcelModel;
import work.soho.common.data.excel.view.DefaultExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@Scope("prototype")
public class HelloController extends BaseController {
    private final HelloService helloService;

    private final ConfigurableApplicationContext configurableApplicationContext;


    @Value("#{@sohoConfig.getByKey('key2')}")
    private String test;

    @GetMapping("/hello/test")
    public String test() {
        return test;
    }

    @GetMapping("/hello/refresh")
    public String refresh() {

//        configurableApplicationContext.refresh();
        SpringContextHolder.getApplicationContext().getAutowireCapableBeanFactory().destroyBean(HelloController.class);
        //SpringContextHolder.getApplicationContext().getParentBeanFactory().
        return "刷新成功";
    }

//    @PreAuthorize("")
    @GetMapping("/hello/excel")
    @ExcelExport(fileName = "test.xsl", modelClass = Hello.class)
    public Object exportHelloList() {
        List<Hello> list = helloService.list();
        return list;
    }

    @GetMapping("/hello/excel2")
    public ModelAndView excel() {
        List<Hello> list = helloService.list();
        ExcelModel excelModel = new ExcelModel();
        excelModel.setFileName("test.xls").addSheet(list, Hello.class).addSheet(list, Hello.class);
        return new ModelAndView(new DefaultExcelView(), excelModel);
    }

    @GetMapping("/hello/excel3")
    @ExcelExport(fileName = "test.xsl", modelClass = Hello.class)
    public Object exportHelloList2() {
        List<Hello> list = helloService.list();
        return new ExcelModel().addSheet(list);
    }
}
