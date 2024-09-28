package work.soho.admin.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "soho-admin-cloud-biz", contextId = "soho-admin-cloud-biz-resource")
public interface AdminResourceApiServiceFeign {
    @GetMapping("/cloud/admin/adminResource/getResourceKeyListByRole")
    List<String> getResourceKeyListByRole(String roleName);
}
