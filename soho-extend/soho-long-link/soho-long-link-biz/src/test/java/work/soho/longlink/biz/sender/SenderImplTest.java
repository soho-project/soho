package work.soho.longlink.biz.sender;

import junit.framework.TestCase;
import work.soho.longlink.api.enums.OnlineEnum;
import work.soho.longlink.biz.connect.ConnectManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SenderImplTest extends TestCase {

    public void testGetOnlineStatus() {
        FakeConnectManager connectManager = new FakeConnectManager();
        connectManager.bind("u1", "c1");
        connectManager.bind("u2", null);

        SenderImpl sender = new SenderImpl(connectManager);
        Map<String, Integer> status = sender.getOnlineStatus(Arrays.asList("u1", "u2", "u3"));

        assertEquals(Integer.valueOf(OnlineEnum.ONLINE.getId()), status.get("u1"));
        assertEquals(Integer.valueOf(OnlineEnum.NOT_ONLINE.getId()), status.get("u2"));
        assertEquals(Integer.valueOf(OnlineEnum.NOT_ONLINE.getId()), status.get("u3"));
    }

    private static final class FakeConnectManager implements ConnectManager {
        private final HashMap<String, HashSet<String>> uidMap = new HashMap<>();

        void bind(String uid, String connectId) {
            if (connectId == null) {
                uidMap.put(uid, new HashSet<>());
                return;
            }
            uidMap.computeIfAbsent(uid, key -> new HashSet<>()).add(connectId);
        }

        @Override
        public void addConnect(io.netty.channel.ChannelHandlerContext ctx) {
            throw new UnsupportedOperationException("not used");
        }

        @Override
        public void removeConnect(String connectId) {
            throw new UnsupportedOperationException("not used");
        }

        @Override
        public io.netty.channel.ChannelHandlerContext getConnect(String connectId) {
            return null;
        }

        @Override
        public Set<String> getAllConnectId() {
            return Collections.emptySet();
        }

        @Override
        public void bindUid(String connectId, String uid) {
            throw new UnsupportedOperationException("not used");
        }

        @Override
        public void removeConnectIdFromUid(String connectId, String uid) {
            throw new UnsupportedOperationException("not used");
        }

        @Override
        public HashSet<String> getConnectIdListByUid(String uid) {
            return uidMap.get(uid);
        }

        @Override
        public Set<String> getAllUid() {
            return uidMap.keySet();
        }
    }
}
