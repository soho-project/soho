package work.soho.common.data.upload.utils;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import work.soho.test.TestApp;

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

    @Test
    public void testS3Upload() {
        String filePath = UploadUtils.upload("s3", "c/d/e/aaaa.txt", "test by fang");
        System.out.println("test by fang...");
        System.out.println(filePath);
    }
}
