package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.cloud.api.request.SendEmailRequest;
import work.soho.admin.cloud.bridge.feign.AdminEmailServiceFeign;
import work.soho.api.admin.service.EmailApiService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminEmailServiceImpl implements EmailApiService {
    private final AdminEmailServiceFeign adminEmailServiceFeign;

    @Override
    public void sendEmail(String to, String name, Map<String, Object> model) {
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setTo(to);
        sendEmailRequest.setName(name);
        sendEmailRequest.setModel(model);
        adminEmailServiceFeign.sendEmail(sendEmailRequest);
    }

    @Override
    public void sendEmail(String to, String name, Map<String, Object> model, Map<String, String> files) {
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setTo(to);
        sendEmailRequest.setName(name);
        sendEmailRequest.setModel(model);
        sendEmailRequest.setFiles(files);
        adminEmailServiceFeign.sendEmail2(sendEmailRequest);
    }
}
