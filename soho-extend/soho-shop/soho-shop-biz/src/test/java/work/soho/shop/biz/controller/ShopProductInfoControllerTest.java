package work.soho.shop.biz.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.common.core.util.JacksonUtils;
import work.soho.shop.api.request.ProductInfoRequest;
import work.soho.shop.biz.aspect.ShopDataSourceAspect;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
@ComponentScan(basePackages = {"work.soho.shop.biz.aspect", "work.soho.shop.biz"}) // 添加切面包扫描
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({ShopDataSourceAspect.class})
class ShopProductInfoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }
    @Test
    @SohoWithUser(id = 1, username = "admin",  role = "admin")
    void list() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/shop/admin/shopProductInfo/list"))
                //.andExpect(status().is(404))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @SohoWithUser(id = 1, username = "admin",  role = "admin")
    void getDetails() throws Exception {
        mockMvc.perform(get("/shop/admin/shopProductInfo/details/15"))
                //.andExpect(status().is(404))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SohoWithUser(id = 1, username = "admin",  role = "admin")
    void update() throws Exception {
        String jsonStr = "{\"spuCode\":\"1111111\",\"name\":\"1111111111111111111111\",\"subTitle\":\"111111\",\"qty\":\"11\",\"originalPrice\":11,\"sellingPrice\":11,\"shopId\":1,\"mainImage\":\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/upload/30/1e/dd301edd30b086605206745b847d81e58c.png\",\"thumbnails\":\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/upload/30/1e/dd301edd30b086605206745b847d81e58c.png\",\"detailHtml\":\"<p><img src=\\\"https://igogo-test.oss-cn-shenzhen.aliyuncs.com/upload/c8/56/79c85679a1165533b2aa7eb62d5dd9be4f.jpg\\\" width=\\\"500\\\" height=\\\"313\\\"></p><p><br></p><p><br></p><p><br></p><p><br></p>\",\"commentCount\":11,\"categoryId\":\"1\",\"shelfStatus\":\"1\",\"auditStatus\":10,\"specs_1\":[\"黑色\"],\"specs_2\":[\"4G\",\"5G\"],\"skus\":{\"1:黑色;2:4G\":{\"code\":\"1\",\"originalPrice\":1,\"sellingPrice\":1,\"qty\":1},\"1:黑色;2:5G\":{\"code\":\"2\",\"originalPrice\":2,\"sellingPrice\":2,\"qty\":2}},\"id\":1}";

        ProductInfoRequest productInfoRequest = JacksonUtils.toBean(jsonStr, ProductInfoRequest.class);
        System.out.println(productInfoRequest);

        productInfoRequest.setId(null);


        mockMvc.perform(post("/shop/admin/shopProductInfo/update")
                        .content(JacksonUtils.toJson(productInfoRequest))
                        .contentType("application/json")
                )
                //.andExpect(status().is(404))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}