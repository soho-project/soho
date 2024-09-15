package work.soho.admin.service.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.api.service.SmsApiService;
import work.soho.test.TestApp;

import java.util.HashMap;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class AdminSmsTemplateServiceImplTest {
    @Autowired
    private SmsApiService smsApiService;

    @Test
    void sendSms() {
        System.out.println("test");
        HashMap<String, String> model = new HashMap<>();
        model.put("code", "1234");
//        smsApiService.sendSms("", "code", model);
    }
}
