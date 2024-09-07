package work.soho.admin.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import work.soho.api.admin.request.AdminConfigInitRequest;

//@FeignClient(value = "soho-admin-cloud-biz")
@FeignClient(name = "soho-admin-cloud-biz", contextId = "soho-admin-cloud-biz-1")
public interface AdminConfigApiServiceFeign {
    /**
     * 获取指定key的值
     * @param key
     * @return
     */
    @GetMapping("/cloud/admin/adminConfig/getByKey")
    String getByKey(@RequestParam("key") String key);

    @PostMapping("/cloud/admin/adminConfig/initItems")
    Boolean initItems(AdminConfigInitRequest adminConfigInitRequest);
}
