package work.soho.common.data.upload.utils;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;
import work.soho.test.TestApp;

import java.io.Serializable;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class UploadUtilsTest {

//    @Autowired
//    private JavaMailSender javaMailSender;

    @Test
    public void testUpload() {
        String filePath = UploadUtils.upload("smb", "c/d/e/fabctest.txt", "test by fang");
        System.out.println("test by fang...");
        System.out.println(filePath);
    }

    @Test
    public void testMail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("test@lisa.org.cn");
        simpleMailMessage.setTo("i@liufang.org.cn");
        simpleMailMessage.setSubject("test by fang");
        simpleMailMessage.setText("test by fang");
        simpleMailMessage.setSentDate(new Date());
//        javaMailSender.send(simpleMailMessage);
    }
}


@Data
public class MailRequest implements Serializable {
    private String sendTo;
    private String subject;
    private String test;
    private String filePath;
}
