package work.soho.admin.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import work.soho.admin.api.request.AdminConfigInitRequest;

@FeignClient(name = "soho-admin-cloud-biz", contextId = "soho-admin-cloud-biz-1")
public interface AdminConfigApiServiceFeign {
    /**
     * 获取指定key的值
     *
     * @param key
     * @return
     */
    @GetMapping("/cloud/admin/adminConfig/getByKey")
    String getByKey(@RequestParam("key") String key);

    /**
     * 传参执行指定脚本
     *
     * @param adminConfigInitRequest
     * @return
     */
    @PostMapping("/cloud/admin/adminConfig/initItems")
    Boolean initItems(AdminConfigInitRequest adminConfigInitRequest);
}
