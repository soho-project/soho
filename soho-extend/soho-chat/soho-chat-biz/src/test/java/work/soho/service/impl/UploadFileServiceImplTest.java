package work.soho.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.service.UploadFileService;
import work.soho.test.TestApp;
import work.soho.upload.api.Upload;
import work.soho.upload.api.vo.UploadInfoVo;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class UploadFileServiceImplTest {

    @Autowired
    private Upload uploadFileService;

    @Test
    void save() throws NoSuchAlgorithmException, IOException {
        String data = "data:image/gif;base64,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";

        UploadInfoVo uploadInfoVo = uploadFileService.save(data);
        System.out.println(uploadInfoVo);
    }

    @Test
    void save2() throws NoSuchAlgorithmException, IOException {

        String data = "https://img.zcool.cn/community/0134ab5b72777fa801206a3538b20b.gif";
        UploadInfoVo uploadInfoVo = uploadFileService.save(data);
        System.out.println(uploadInfoVo);
    }
}
