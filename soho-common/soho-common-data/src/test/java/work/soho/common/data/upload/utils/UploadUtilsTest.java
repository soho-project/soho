package work.soho.common.data.upload.utils;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;
import work.soho.test.TestApp;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class UploadUtilsTest {

    @Test
    public void testUpload() {
        String filePath = UploadUtils.upload("smb", "c/d/e/fabctest.txt", "test by fang");
        System.out.println("test by fang...");
        System.out.println(filePath);
    }
}
