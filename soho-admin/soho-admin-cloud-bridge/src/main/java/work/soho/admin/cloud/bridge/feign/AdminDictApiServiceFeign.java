package work.soho.admin.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "soho-admin-cloud-biz", contextId = "soho-admin-cloud-biz-2")
public interface AdminDictApiServiceFeign {
    @GetMapping("/cloud/admin/adminDict/getMapByCode")
    String getByKey(@RequestParam("code") String code);

    @GetMapping("/cloud/admin/adminDict/getOptionsByCode")
    String getOptionsByCode(@RequestParam("code") String code);
}
