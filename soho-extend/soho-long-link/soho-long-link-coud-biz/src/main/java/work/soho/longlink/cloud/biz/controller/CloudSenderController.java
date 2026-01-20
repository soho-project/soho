package work.soho.longlink.cloud.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.longlink.api.sender.Sender;
import work.soho.longlink.cloud.api.request.SenderBindUidRequest;
import work.soho.longlink.cloud.api.request.SenderSendToConnectIdRequest;
import work.soho.longlink.cloud.api.request.SenderSendToUidRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud/sender")
public class CloudSenderController {
    private final Sender sender;

    @PostMapping("sendToUid")
    public void sendToUid(@RequestBody SenderSendToUidRequest request) {
        sender.sendToUid(request.getUid(), request.getMsg());
    }

    @PostMapping("sendToConnectId")
    public void sendToConnectId(@RequestBody SenderSendToConnectIdRequest request) {
        sender.sendToConnectId(request.getConnectId(), request.getMsg());
    }

    @PostMapping("sendToAllUid")
    public void sendToAllUid(@RequestBody String msg) {
        sender.sendToAllUid(msg);
    }

    @PostMapping("sendToAllConnect")
    public void sendToAllConnect(@RequestBody String msg) {
        sender.sendToAllConnect(msg);
    }

    @PostMapping("bindUid")
    public void bindUid(@RequestBody SenderBindUidRequest request) {
        sender.bindUid(request.getConnectId(), request.getUid());
    }
}
