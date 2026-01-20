package work.soho.longlink.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import work.soho.longlink.cloud.api.request.SenderBindUidRequest;
import work.soho.longlink.cloud.api.request.SenderSendToConnectIdRequest;
import work.soho.longlink.cloud.api.request.SenderSendToUidRequest;


@FeignClient(name = "soho-long-link-coud-biz", contextId = "soho-long-link-coud-biz-1")
public interface SenderServiceFeign {
    @PostMapping("/cloud/sender/sendToUid")
    void sendToUid(@RequestBody SenderSendToUidRequest request);

    @PostMapping("/cloud/sender/sendToConnectId")
    void sendToConnectId(@RequestBody SenderSendToConnectIdRequest  request);

    @PostMapping("/cloud/sender/sendToAllUid")
    void sendToAllUid(@RequestBody String msg);

    @PostMapping("/cloud/sender/sendToAllConnect")
    void sendToAllConnect(@RequestBody String msg);

    @PostMapping("/cloud/sender/bindUid")
    void bindUid(@RequestBody SenderBindUidRequest request);
}
