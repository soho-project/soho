package work.soho.longlink.biz.sender;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.enums.OnlineEnum;
import work.soho.longlink.api.sender.QueryLongLink;
import work.soho.longlink.biz.connect.ConnectManager;
import work.soho.longlink.api.sender.Sender;

import java.util.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class SenderImpl implements Sender, QueryLongLink {
    private final ConnectManager connectManager;

    @Override
    public void sendToUid(String uid, String msg) {
        Set<String> uidSet = connectManager.getConnectIdListByUid(uid);
        if(uidSet == null) {
            return;
        }
        uidSet.forEach(connectId -> {
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
        connectManager.getAllUid().forEach(uid -> {
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

    /**
     * 获取用户在线状态
     *
     * @param uids
     * @return
     */
    @Override
    public Map<String, Integer> getOnlineStatus(List<String> uids) {
        HashMap<String, Integer> userStatus = new HashMap<>();
        uids.forEach(uid->{
            HashSet<String> connects = connectManager.getConnectIdListByUid(uid);
            if(connects == null || connects.size()==0) {
                //不在线
                userStatus.put(uid, OnlineEnum.NOT_ONLINE.getId());
            } else {
                userStatus.put(uid, OnlineEnum.ONLINE.getId());
            }
        });
        return userStatus;
    }
}
