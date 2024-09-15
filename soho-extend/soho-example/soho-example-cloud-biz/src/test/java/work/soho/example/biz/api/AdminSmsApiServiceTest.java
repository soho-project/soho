package work.soho.example.biz.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.api.service.AdminInfoApiService;
import work.soho.admin.api.service.SmsApiService;
import work.soho.admin.api.vo.AdminUserVo;

import java.util.HashMap;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest
public class AdminSmsApiServiceTest {
    @Autowired
    private SmsApiService service;

    @Autowired
    private AdminInfoApiService adminInfoApiService;

    @Test
    public void sendSms()
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "123456");
        service.sendSms("15873167777", "fang", params);
        System.out.println("hello");
    }

    @Test
    public void getAdminById() {
        AdminUserVo info = adminInfoApiService.getAdminById(1l);
        System.out.println(info);
    }
}
