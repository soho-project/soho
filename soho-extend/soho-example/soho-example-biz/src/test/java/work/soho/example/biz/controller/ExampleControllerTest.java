package work.soho.example.biz.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class ExampleControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }


    @Test
    @SohoWithUser(id = 6, username = "admin",  role = "admin")
    void importExcel() throws Exception {
        System.out.println("hello");
        // 创建一个虚拟的 Excel 文件
        ClassPathResource resource = new ClassPathResource("example2.xls");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "example.xls",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                resource.getInputStream()
        );

        // 发送请求并验证返回状态
        mockMvc.perform(multipart("/admin/example/importExcel")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}