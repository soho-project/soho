package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.cloud.api.request.SendSmsRequest;
import work.soho.admin.cloud.bridge.feign.AdminSmsServiceFeign;
import work.soho.api.admin.service.SmsApiService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminSmsServiceImpl  implements SmsApiService {
    private final AdminSmsServiceFeign adminSmsServiceFeign;
    @Override
    public void sendSms(String phone, String name, Map<String, String> model) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhone(phone);
        sendSmsRequest.setName(name);
        sendSmsRequest.setModel(model);
        adminSmsServiceFeign.sendSms(sendSmsRequest);
    }
}
