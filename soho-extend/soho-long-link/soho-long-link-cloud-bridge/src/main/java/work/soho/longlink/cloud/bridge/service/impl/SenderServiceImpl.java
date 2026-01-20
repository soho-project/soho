package work.soho.longlink.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.longlink.api.sender.Sender;
import work.soho.longlink.cloud.api.request.SenderBindUidRequest;
import work.soho.longlink.cloud.api.request.SenderSendToConnectIdRequest;
import work.soho.longlink.cloud.api.request.SenderSendToUidRequest;
import work.soho.longlink.cloud.bridge.feign.SenderServiceFeign;

@Service
@RequiredArgsConstructor
public class SenderServiceImpl implements Sender {
    private final SenderServiceFeign senderServiceFeign;

    @Override
    public void sendToUid(String uid, String msg) {
        SenderSendToUidRequest request = new SenderSendToUidRequest();
        request.setUid(uid);
        request.setMsg(msg);
        senderServiceFeign.sendToUid(request);
    }

    @Override
    public void sendToConnectId(String connectId, String msg) {
        SenderSendToConnectIdRequest request = new SenderSendToConnectIdRequest();
        request.setConnectId(connectId);
        request.setMsg(msg);
        senderServiceFeign.sendToConnectId(request);
    }

    @Override
    public void sendToAllUid(String msg) {
        senderServiceFeign.sendToAllUid(msg);
    }

    @Override
    public void sendToAllConnect(String msg) {
        senderServiceFeign.sendToAllConnect(msg);
    }

    @Override
    public void bindUid(String connectId, String uid) {
        SenderBindUidRequest request = new SenderBindUidRequest();
        request.setConnectId(connectId);
        request.setUid(uid);
        senderServiceFeign.bindUid(request);
    }
}
