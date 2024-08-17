package work.soho.code.biz.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTableTemplate;
import work.soho.code.biz.service.impl.TestCodeTableTemplateServiceImpl;
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
    private TestCodeTableTemplateServiceImpl codeTableTemplateService;

    // 表名  这个应该是固定的专门为测试定义的表
    private static final Integer TABLE_ID = 155439140;

    // 代码临时目录
    private static final String CODE_TMP_DIR = "/home/fang/work/java/admin/soho-extend/soho-example/soho-example-biz";

    private String getCode(Integer templateId) {
        try {
            Integer tableId =  TABLE_ID;  //测试表ID
            HashMap<String, String> binds = new HashMap<>();
            binds.put("baseNamespace", "work.soho.example."); //基本命名空间
            binds.put("basePath", CODE_TMP_DIR); //基本写入路径

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
            return code;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 根据名称测试
     *
     * @param name
     * @param save
     * @return
     */
    private String getCodeByTemplateName(String name, Boolean save) {
        LambdaQueryWrapper<CodeTableTemplate> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(CodeTableTemplate::getName, name);
        CodeTableTemplate codeTableTemplate = codeTableTemplateService.getOne(lambdaQuery);
        Assert.notNull(codeTableTemplate);
        String code = getCode(codeTableTemplate.getId());

        if(code != null && save && false) {
            //保存到数据库
            codeTableTemplateService.saveLocal2Db(codeTableTemplate.getId());
            //保存公共类库
            codeTableTemplateService.saveLocal2Db(1);
        }

        return code;
    }

    // 测试后台admin controller
    @Test
    void testAdminController() {
        // adminJavaController
        String code = getCodeByTemplateName("adminJavaController", true);
        System.out.println(code);
    }
    // 测试后台admin enums
    @Test
    void testAdminEnums() {
        // adminJavaController
        String code = getCodeByTemplateName("enums", true);
        System.out.println(code);
    }

    // 前端model 测试
    @Test
    void testReactModel() {
        // h5 react filter
        String code = getCodeByTemplateName("ReactModel", true);
        System.out.println(code);
    }

    @Test
    void testReactFilter() {
        // h5 react filter
        String code = getCodeByTemplateName("reactFilter", true);
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