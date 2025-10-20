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
@ActiveProfiles("dev")
class GroovyServiceTest {
    @Autowired
    private GroovyService groovyService;

    @Autowired
    private CodeTableService codeTableService;

    @Autowired
    private TestCodeTableTemplateServiceImpl codeTableTemplateService;

    // 表名  这个应该是固定的专门为测试定义的表 155439140
//    private static final Integer TABLE_ID = 155439140;  // 测试用例表
//    private static final Integer TABLE_ID = 155439128;  // 内容表
//    private static final Integer TABLE_ID = 155439198;  // shop_coupon_usage_logs
    private static final Integer TABLE_ID = 155439202;  // shop_coupon_usage_logs
//    private static final Integer TABLE_ID = 155439180;  // 计划任务表
//    private static final Integer TABLE_ID = 155439179; // 系统资源表， tree

    private String moduleName = "shop";

    //example 分类表ID
//    private static final Integer EXAMPLE_CATEGORY_TABLE_ID = 155439141;

    // 代码临时目录
    private static final String CODE_TMP_DIR = "/home/fang/work/java/admin/soho-extend/soho-example/soho-example-biz";

    private String getCode(Integer templateId) {
        try {
            Integer tableId =  TABLE_ID;  //测试表ID
//            Integer tableId =  EXAMPLE_CATEGORY_TABLE_ID;  //测试表ID
            HashMap<String, String> binds = new HashMap<>();
            binds.put("baseNamespace", "work.soho.example."); //基本命名空间
            binds.put("basePath", CODE_TMP_DIR); //基本写入路径
            binds.put("moduleName", moduleName);

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

        if(code != null && save) {
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

    // 测试后台admin domain
    @Test
    void testAdminDomain() {
        // adminJavaController
        String code = getCodeByTemplateName("javaDomain", true);
        System.out.println(code);
    }

    // 前端model 测试
    @Test
    void testReactModel() {
        // h5 react filter
        String code = getCodeByTemplateName("ReactModel", true);
        System.out.println(code);
    }
    // 前端 reactIndex 测试
    @Test
    void testReactIndex() {
        // h5 react filter
        String code = getCodeByTemplateName("reactIndex", true);
        System.out.println(code);
    }
    @Test
    void testReactTree() {
        // h5 react filter
        String code = getCodeByTemplateName("React Tree", true);
        System.out.println(code);
    }

    @Test
    void testReactFilter() {
        // h5 react filter
        String code = getCodeByTemplateName("reactFilter", true);
        System.out.println(code);
    }

    @Test
    void testReactList() {
        // h5 react filter
        String code = getCodeByTemplateName("reactList", true);
        System.out.println(code);
    }

    /**
     * 详情模型
     */
    @Test
    void testReactDetailsModel() {
        // h5 react filter
        String code = getCodeByTemplateName("reactDetailsModel", true);
        System.out.println(code);
    }

    /**
     * 详情页面
     */
    @Test
    void testReactDetails() {
        // h5 react filter
        String code = getCodeByTemplateName("reactDetails", true);
        System.out.println(code);
    }

    @Test
    void testAntd5Index() {
        String code = getCodeByTemplateName("antd5Index", true);
        System.out.println(code);
    }

    @Test
    void testAntd5DetailsModal() {
        String code = getCodeByTemplateName("antd5DetailsModal", true);
        System.out.println(code);
    }

    @Test
    void testAntd5Filter() {
        String code = getCodeByTemplateName("antd5Filter", true);
        System.out.println(code);
    }

    @Test
    void testAntd5List() {
        String code = getCodeByTemplateName("antd5List", true);
        System.out.println(code);
    }

    @Test
    void testAntd5Details() {
        String code = getCodeByTemplateName("antd5Details", true);
        System.out.println(code);
    }

    @Test
    void testAntd5TreeIndex() {
        String code = getCodeByTemplateName("antd5TreeIndex", true);
        System.out.println(code);
    }

    @Test
    void testAntd5ExtendedComponents() {
        String code = getCodeByTemplateName("antd5ExtendedComponents", true);
        System.out.println(code);
    }

    @Test
    void testAntd5Apis() {
        String code = getCodeByTemplateName("antd5Apis", true);
        System.out.println(code);
    }

    @Test
    void testVue3Apis() {
        String code = getCodeByTemplateName("vue3-apis", true);
        System.out.println(code);
    }

    @Test
    void testVue3Editor() {
        String code = getCodeByTemplateName("vue3-editorModal", true);
        System.out.println(code);
    }

    @Test
    void testVue3List() {
        String code = getCodeByTemplateName("vue3-list", true);
        System.out.println(code);
    }

    @Test
    void testVue3Filter() {
        String code = getCodeByTemplateName("vue3-filter", true);
        System.out.println(code);
    }

    @Test
    void testVue3Index() {
        String code = getCodeByTemplateName("vue3-index", true);
        System.out.println(code);
    }

    @Test
    void testVue3TreeIndex() {
        String code = getCodeByTemplateName("vue3-tree", true);
        System.out.println(code);
    }

    @Test
    void testVue3ExtendedComponents() {
        String code = getCodeByTemplateName("vue3-extendedComponents", true);
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