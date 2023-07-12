package work.soho.longlink.biz.sender;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.connect.ConnectManager;
import work.soho.longlink.api.sender.Sender;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SenderImpl implements Sender {
    private final ConnectManager connectManager;

    @Override
    public void sendToUid(String uid, String msg) {
        Set<String> uidSet = connectManager.getConnectIdListByUid(uid);
        uidSet.stream().forEach(connectId -> {
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
