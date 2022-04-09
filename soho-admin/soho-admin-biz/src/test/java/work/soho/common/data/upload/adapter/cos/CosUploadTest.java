package work.soho.common.data.upload.adapter.cos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.admin.AdminApplication;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class CosUploadTest {
    @Autowired
    private CosUpload cosUpload;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void upload() {
        cosUpload.uploadFile("/test/hello.txt", "hello world");
    }
}