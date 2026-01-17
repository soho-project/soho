package work.soho.longlink.biz.connect;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.util.ServerUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectManagerImpl implements ConnectManager {
    private final ConcurrentHashMap<String, ChannelHandlerContext> connects = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Set<String>> connectIdUidMap = new ConcurrentHashMap<>();

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
        return new HashSet<>(connects.keySet());
    }

    @Override
    public void bindUid(String connectId, String uid) {
        connectIdUidMap.computeIfAbsent(uid, key -> ConcurrentHashMap.newKeySet())
                .add(connectId);
    }

    @Override
    public void removeConnectIdFromUid(String connectId, String uid) {
        if(uid == null) {
            return;
        }
        Set<String> setConnectId = connectIdUidMap.get(uid);
        if (setConnectId == null) {
            return;
        }
        setConnectId.remove(connectId);
        if (setConnectId.isEmpty()) {
            connectIdUidMap.remove(uid, setConnectId);
        }
    }

    @Override
    public HashSet<String> getConnectIdListByUid(String uid) {
        Set<String> set = connectIdUidMap.get(uid);
        if (set == null) {
            return null;
        }
        return new HashSet<>(set);
    }

    @Override
    public Set<String> getAllUid() {
        return new HashSet<>(connectIdUidMap.keySet());
    }
}
