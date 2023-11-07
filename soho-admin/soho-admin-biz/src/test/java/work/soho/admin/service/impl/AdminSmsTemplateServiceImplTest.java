package work.soho.admin.service.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.AdminApplication;
import work.soho.api.admin.service.SmsApiService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
@Log4j2
class AdminSmsTemplateServiceImplTest {
    @Autowired
    private SmsApiService smsApiService;

    @Test
    void sendSms() {
        System.out.println("test");
        HashMap<String, Object> model = new HashMap<>();
        model.put("code", "1234");
//        smsApiService.sendSms("test", model);
    }
}
