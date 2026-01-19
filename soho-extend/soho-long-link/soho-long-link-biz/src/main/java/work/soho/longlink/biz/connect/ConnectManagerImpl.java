package work.soho.longlink.biz.connect;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.metrics.LongLinkMetrics;
import work.soho.longlink.biz.util.ServerUtil;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ConnectManagerImpl implements ConnectManager {
    private final LongLinkMetrics metrics;
    private final ConcurrentHashMap<String, ChannelHandlerContext> connects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConnectInfo> connectInfoMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Set<String>> connectIdUidMap = new ConcurrentHashMap<>();

    @Override
    public void addConnect(ChannelHandlerContext ctx) {
        String connectId = ServerUtil.getConnectId(ctx);
        connects.put(connectId, ctx);
        connectInfoMap.put(connectId, buildConnectInfo(connectId, ctx));
        metrics.recordNewConnection();
    }

    @Override
    public void removeConnect(String connectId) {
        connects.remove(connectId);
        connectInfoMap.remove(connectId);
        metrics.recordCloseConnection();
    }

    @Override
    public ChannelHandlerContext getConnect(String connectId) {
        return connects.get(connectId);
    }

    @Override
    public ConnectInfo getConnectInfo(String connectId) {
        return connectInfoMap.get(connectId);
    }

    @Override
    public void recordReceivedMessage(String connectId) {
        metrics.recordReceivedMessage();
        if (connectId == null) {
            return;
        }
        ConnectInfo info = connectInfoMap.get(connectId);
        if (info != null) {
            info.recordReceivedMessage();
        }
    }

    @Override
    public void recordSentMessage(String connectId) {
        metrics.recordSentMessage();
        if (connectId == null) {
            return;
        }
        ConnectInfo info = connectInfoMap.get(connectId);
        if (info != null) {
            info.recordSentMessage();
        }
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

    private ConnectInfo buildConnectInfo(String connectId, ChannelHandlerContext ctx) {
        InetSocketAddress clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress serverAddress = (InetSocketAddress) ctx.channel().localAddress();
        String clientIp = clientAddress == null ? null : clientAddress.getAddress().getHostAddress();
        Integer clientPort = clientAddress == null ? null : clientAddress.getPort();
        String serverIp = serverAddress == null ? null : serverAddress.getAddress().getHostAddress();
        Integer serverPort = serverAddress == null ? null : serverAddress.getPort();
        return new ConnectInfo(connectId, clientIp, clientPort, serverIp, serverPort, System.currentTimeMillis());
    }
}
