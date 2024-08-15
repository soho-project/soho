package work.soho.code.biz.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTableTemplate;
import work.soho.test.TestApp;

import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class GroovyServiceTest {
    @Autowired
    private GroovyService groovyService;

    @Autowired
    private CodeTableService codeTableService;

    @Autowired
    private CodeTableTemplateService codeTableTemplateService;

    @Test
    void invoke03() {
    }

    @Test
    void invoke() {
    }

    @Test
    void runById() throws InvalidPropertiesFormatException {
        Integer tableId =  155439140;  //测试表ID
//        Integer tableId =  155439172;  //测试表ID operation log
//        Integer templateId = 1170362371; //测试模板ID  java 控制器
//        Integer templateId = 1170362373; //测试模板ID  java Domain 类
        Integer templateId = 1170362383; //测试模板ID  前端 reactFilter

        HashMap<String, String> binds = new HashMap<>();
        binds.put("baseNamespace", "work.soho.example."); //基本命名空间
        binds.put("basePath", "/tmp"); //基本写入路径

        CodeTableVo codeTableVo = codeTableService.getTableVoById(tableId);
        if(codeTableVo == null) {
            throw new InvalidPropertiesFormatException("请传递正确的表ID");
        }
        //获取模板信息
        CodeTableTemplate codeTableTemplate = codeTableTemplateService.getById(templateId);
        if(codeTableTemplate == null) {
            throw  new InvalidPropertiesFormatException("模板ID（"+templateId+"）不存在");
        }
        String code = groovyService.runById(templateId, binds, "getCode", codeTableVo);
        System.out.println(code);
    }

    @Test
    void testRunById() {
    }

    @Test
    void runByName() {
    }

    @Test
    void invokeByIdWithName() {
    }
}