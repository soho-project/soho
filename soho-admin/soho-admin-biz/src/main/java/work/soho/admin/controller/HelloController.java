package work.soho.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.xslt.XsltView;
import work.soho.admin.domain.Hello;
import work.soho.admin.service.HelloService;
import work.soho.admin.view.DefaultExcelView;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.data.excel.model.ExcelModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RestController
@RequiredArgsConstructor
public class HelloController extends BaseController {
    private final HelloService helloService;

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
