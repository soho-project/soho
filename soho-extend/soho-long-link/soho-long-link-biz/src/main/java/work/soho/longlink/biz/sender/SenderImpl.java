package work.soho.longlink.biz.sender;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.connect.ConnectManager;
import work.soho.longlink.api.sender.Sender;

import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class SenderImpl implements Sender {
    private final ConnectManager connectManager;

    @Override
    public void sendToUid(String uid, String msg) {
        Set<String> uidSet = connectManager.getConnectIdListByUid(uid);
        if(uidSet == null) {
            return;
        }
        uidSet.stream().forEach(connectId -> {
            log.info("send message to uid:{}， cliendId:{} message:{}", uid, connectId, msg);
            sendToConnectId(connectId, msg);
        });
    }

    @Override
    public void sendToConnectId(String connectId, String msg) {
        ChannelHandlerContext context = connectManager.getConnect(connectId);
        if(context != null) {
            context.writeAndFlush(new TextWebSocketFrame(msg));
        }
    }

    @Override
    public void sendToAllUid(String msg) {
        connectManager.getAllUid().stream().forEach(uid -> {
            sendToUid(uid, msg);
        });
    }

    @Override
    public void sendToAllConnect(String msg) {
        connectManager.getAllConnectId().forEach(connectId -> {
            sendToConnectId(connectId, msg);
        });
    }

    @Override
    public void bindUid(String connectId, String uid) {
        connectManager.bindUid(connectId, uid);
    }
}
