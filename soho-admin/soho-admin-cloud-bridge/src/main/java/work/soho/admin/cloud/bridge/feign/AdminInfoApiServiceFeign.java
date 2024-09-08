package work.soho.admin.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import work.soho.api.admin.vo.AdminUserVo;
import java.util.HashSet;

@FeignClient(name = "soho-admin-cloud-biz", contextId = "soho-admin-cloud-biz-info")
public interface AdminInfoApiServiceFeign {
    @GetMapping("/cloud/admin/adminInfo/getAdminById")
    AdminUserVo getAdminById(@RequestParam("id") Long id);

    @GetMapping("/cloud/admin/adminInfo/getResourceKeys")
    HashSet<String> getResourceKeys(@RequestParam("id") Long id);
}
