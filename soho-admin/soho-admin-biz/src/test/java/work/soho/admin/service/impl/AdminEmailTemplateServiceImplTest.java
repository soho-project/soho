package work.soho.admin.service.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.AdminApplication;
import work.soho.api.admin.service.EmailApiService;
import work.soho.test.TestApp;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class AdminEmailTemplateServiceImplTest {
    @Autowired
    private EmailApiService emailApiService;

    @Test
    public void sendEmail() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "我是传递的标题");
        model.put("code", "1234");
        emailApiService.sendEmail( "i@liufang.org.cn", "test", model);
    }
}
