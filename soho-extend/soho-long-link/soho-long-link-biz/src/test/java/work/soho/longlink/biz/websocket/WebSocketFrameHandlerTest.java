package work.soho.longlink.biz.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import junit.framework.TestCase;
import work.soho.longlink.api.authentication.Authentication;
import work.soho.longlink.api.chanel.MessageChanel;
import work.soho.longlink.biz.connect.ConnectManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WebSocketFrameHandlerTest extends TestCase {

    public void testPingPong() {
        CapturingMessageChannel messageChanel = new CapturingMessageChannel();
        WebSocketFrameHandler handler = new WebSocketFrameHandler(
                messageChanel,
                new NoopConnectManager()
        );

        EmbeddedChannel channel = new EmbeddedChannel(handler);
        channel.writeInbound(new TextWebSocketFrame("ping"));

        TextWebSocketFrame outbound = channel.readOutbound();
        assertNotNull(outbound);
        assertEquals("pong", outbound.text());
        outbound.release();
    }

    public void testTextMessageDispatch() throws Exception {
        CapturingMessageChannel messageChanel = new CapturingMessageChannel();
        WebSocketFrameHandler handler = new WebSocketFrameHandler(
                messageChanel,
                new NoopConnectManager()
        );
        setPrivateField(handler, "connectId", "cid-1");

        EmbeddedChannel channel = new EmbeddedChannel(handler);
        channel.attr(AuthHandshakeHandler.ATTR_USER_ID).set("u1");
        channel.writeInbound(new TextWebSocketFrame("hello"));

        assertEquals("hello", messageChanel.message);
        assertEquals("cid-1", messageChanel.connectId);
        assertEquals("u1", messageChanel.uid);
    }

    private static void setPrivateField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static final class DummyAuthentication implements Authentication {
        @Override
        public String getUidWithToken(String token) {
            return null;
        }
    }

    private static final class CapturingMessageChannel implements MessageChanel {
        private String message;
        private String connectId;
        private String uid;

        @Override
        public void onMessage(String message) {
            this.message = message;
        }

        @Override
        public void onMessage(String message, String connectId, String uid) {
            this.message = message;
            this.connectId = connectId;
            this.uid = uid;
        }
    }

    private static final class NoopConnectManager implements ConnectManager {
        @Override
        public void addConnect(ChannelHandlerContext ctx) {
        }

        @Override
        public void removeConnect(String connectId) {
        }

        @Override
        public ChannelHandlerContext getConnect(String connectId) {
            return null;
        }

        @Override
        public work.soho.longlink.biz.connect.ConnectInfo getConnectInfo(String connectId) {
            return null;
        }

        @Override
        public Set<String> getAllConnectId() {
            return Collections.emptySet();
        }

        @Override
        public void bindUid(String connectId, String uid) {
        }

        @Override
        public void removeConnectIdFromUid(String connectId, String uid) {
        }

        @Override
        public HashSet<String> getConnectIdListByUid(String uid) {
            return null;
        }

        @Override
        public Set<String> getAllUid() {
            return Collections.emptySet();
        }

        @Override
        public void recordReceivedMessage(String connectId) {
        }

        @Override
        public void recordSentMessage(String connectId) {
        }
    }
}
