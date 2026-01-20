package work.soho.longlink.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "soho-long-link-coud-biz", contextId = "soho-long-link-coud-biz-2")
public interface CloudQueryLongLinkFeign {
    @PostMapping("/cloud/queryLongLink/getOnlineStatus")
    Map<String, Integer> getOnlineStatus(@RequestBody List<String> uids);
}
