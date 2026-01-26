package work.soho.content.biz.wordpress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WordPress WXR 导入 MVC 测试
 */
@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
public class WordPressWxrImportMvcTest {
    private static final String DEFAULT_XML_PATH = "/home/fang/Downloads/WordPress.2026-01-26.xml";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SohoWithUser(id = 1, username = "admin", role = "admin")
    void importWxrViaMvc() throws Exception {
        String xmlPath = getValue("wp.test.xml.path", "WP_TEST_XML_PATH", DEFAULT_XML_PATH);
        File xmlFile = new File(xmlPath);
        assertTrue(xmlFile.exists(), "WXR 文件不存在: " + xmlFile.getAbsolutePath());

        FileSystemResource resource = new FileSystemResource(xmlFile);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                xmlFile.getName(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                resource.getInputStream()
        );

        mockMvc.perform(multipart("/content/admin/wordpress/import-wxr").file(file))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private static String getValue(String sysKey, String envKey, String fallback) {
        String value = System.getProperty(sysKey);
        if (value == null || value.isEmpty()) {
            value = System.getenv(envKey);
        }
        return (value == null || value.isEmpty()) ? fallback : value;
    }

}
