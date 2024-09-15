package work.soho.admin.cloud.biz.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.service.SmsApiService;

import java.util.Map;

@RestController
@RequestMapping("/cloud/admin/adminSms")
@RequiredArgsConstructor
public class CloudAdminSmsController {
    private final SmsApiService smsApiService;

    @PostMapping("sendSms")
    public void sendSms(String phone, String name, Map<String,String> model) {
        smsApiService.sendSms(phone, name, model);
    }
}
