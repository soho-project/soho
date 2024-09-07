package work.soho.admin.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import work.soho.admin.cloud.api.request.SendSmsRequest;

@FeignClient(name = "soho-admin-cloud-biz", contextId = "soho-admin-cloud-biz-adminBiz")
public interface AdminSmsServiceFeign {
    @PostMapping("/cloud/admin/adminSms/sendSms")
    void sendSms(@RequestBody SendSmsRequest sendSmsRequest);
}
