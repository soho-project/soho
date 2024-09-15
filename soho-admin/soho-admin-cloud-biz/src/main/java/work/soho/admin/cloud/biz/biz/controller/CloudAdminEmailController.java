package work.soho.admin.cloud.biz.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.cloud.api.request.SendEmailRequest;
import work.soho.admin.api.service.EmailApiService;

@RestController
@RequestMapping("/cloud/admin/adminEmail")
@RequiredArgsConstructor
public class CloudAdminEmailController {
    private final EmailApiService emailApiService;

    @PostMapping(value ="sendEmail1")
    public void sendEmail(@RequestBody SendEmailRequest request) {
        emailApiService.sendEmail(request.getTo(), request.getName(), request.getModel());
    }

    @PostMapping("sendEmail2")
    public void sendEmail2(@RequestBody SendEmailRequest request) {
        emailApiService.sendEmail(request.getTo(), request.getName(), request.getModel(), request.getFiles());
    }
}
