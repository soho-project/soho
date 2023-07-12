package work.soho.longlink.biz.connect;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.util.ServerUtil;

import java.util.*;

@Component
public class ConnectManagerImpl implements ConnectManager {
    private HashMap<String, ChannelHandlerContext> connects = new HashMap<>();

    private HashMap<String, HashSet<String>> connectIdUidMap = new HashMap<>();

    @Override
    public void addConnect(ChannelHandlerContext ctx) {
        connects.put(ServerUtil.getConnectId(ctx), ctx);
    }

    @Override
    public void removeConnect(String connectId) {
        connects.remove(connectId);
    }

    @Override
    public ChannelHandlerContext getConnect(String connectId) {
        return connects.get(connectId);
    }

    @Override
    public Set<String> getAllConnectId() {
        return connects.keySet();
    }

    @Override
    public void bindUid(String connectId, String uid) {
        HashSet<String> connectIdList = connectIdUidMap.get(uid);
        if(connectIdList == null) {
            connectIdList = new HashSet<String>();
            connectIdUidMap.put(uid, connectIdList);
        }
        connectIdList.add(connectId);
    }

    @Override
    public HashSet<String> getConnectIdListByUid(String uid) {
        return connectIdUidMap.get(uid);
    }

    @Override
    public Set<String> getAllUid() {
        return connectIdUidMap.keySet();
    }
}
