package work.soho.admin.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import work.soho.admin.cloud.api.request.SendEmailRequest;

@FeignClient(name = "soho-admin-cloud-biz", contextId = "soho-admin-cloud-biz-email")
public interface AdminEmailServiceFeign {
    @PostMapping("/cloud/admin/adminEmail/sendEmail1")
    String sendEmail(@RequestBody SendEmailRequest sendEmailRequest);

    @PostMapping("/cloud/admin/adminEmail/sendEmail2")
    String sendEmail2(@RequestBody SendEmailRequest sendEmailRequest);
}
