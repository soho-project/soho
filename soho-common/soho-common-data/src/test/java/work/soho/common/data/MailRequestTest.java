package work.soho.common.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import work.soho.test.TestApp;

import java.util.Date;

@ContextConfiguration
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class MailRequestTest {
    @Test
    public void testSendWithMail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("test@lisa.org.cn");
        simpleMailMessage.setTo("i@liufang.org.cn");
        simpleMailMessage.setSubject("test by fang");
        simpleMailMessage.setText("test by fang");
        simpleMailMessage.setSentDate(new Date());
//        javaMailSender.send(simpleMailMessage);
    }
}
