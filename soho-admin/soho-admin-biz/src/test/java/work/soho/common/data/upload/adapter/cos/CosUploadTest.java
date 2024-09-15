package work.soho.common.data.upload.adapter.cos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import work.soho.admin.biz.AdminApplication;
import work.soho.common.data.upload.UploadManage;
import work.soho.common.data.upload.utils.UploadUtils;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class CosUploadTest {

    @Autowired
    private UploadManage uploadManage;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void uploadManage() {
        uploadManage.get("cos2").uploadFile("test/uploadManage.txt", "hello world, upload by manage");
    }

    @Test
    public void utils() {
        UploadUtils.upload("test/utils.txt", "hello utils");
        //阿里云上传
        UploadUtils.upload("oss", "test/utils.txt", "hello utils");
        UploadUtils.upload("cos", "test/utils.txt", "hello utils");
        //腾讯云上传
        UploadUtils.upload("cos2", "test/utils.txt", "hello utils");
    }

    @Test
    public void qiniu() {
        String fullPath = UploadUtils.upload("qiniu", "test/utils.txt", "hello utils");
        System.out.println(fullPath);
        assertNotNull(fullPath);
    }

    @Test
    public void file() {
        String fullPath = UploadUtils.upload("file", "test/utils.txt", "hello utils");
        System.out.println(fullPath);
        assertNotNull(fullPath);
    }
}