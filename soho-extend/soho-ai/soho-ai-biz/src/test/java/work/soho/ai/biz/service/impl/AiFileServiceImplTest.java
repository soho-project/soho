package work.soho.ai.biz.service.impl;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 文件服务测试。
 */
public class AiFileServiceImplTest {

    /**
     * 本地与私网目标应被拦截。
     */
    @Test
    public void isBlockedFileUrl_shouldBlockLocalAndPrivateTargets() {
        AiFileServiceImpl service = new AiFileServiceImpl();

        assertThat(service.isBlockedFileUrl("http://localhost/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://localhost./test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://127.0.0.1/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://192.168.1.10/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://10.0.0.8/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://[::1]/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("ftp://example.com/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("")).isTrue();
        assertThat(service.isBlockedFileUrl("not-a-url")).isTrue();
    }

    /**
     * 公网 HTTP 地址应允许通过基础校验。
     */
    @Test
    public void isBlockedFileUrl_shouldAllowPublicHttpHost() {
        AiFileServiceImpl service = new AiFileServiceImpl();
        assertThat(service.isBlockedFileUrl("https://8.8.8.8/test.txt")).isFalse();
    }

    /**
     * 直接访问被拦截地址时应返回空内容。
     */
    @Test
    public void extractTextFromUrl_whenBlockedUrl_shouldReturnEmpty() {
        AiFileServiceImpl service = new AiFileServiceImpl();
        assertThat(service.extractTextFromUrl("http://127.0.0.1/private.txt")).isEmpty();
    }

    /**
     * 公网首跳若重定向到私网目标应被拦截。
     */
    @Test
    public void extractTextFromUrl_whenRedirectsToPrivateTarget_shouldReturnEmpty() throws Exception {
        TestableAiFileServiceImpl service = new TestableAiFileServiceImpl();
        service.stub("https://8.8.8.8/start.txt", StubHttpURLConnection.redirect("https://8.8.8.8/start.txt", HttpURLConnection.HTTP_MOVED_TEMP, "http://127.0.0.1/private.txt"));

        assertThat(service.extractTextFromUrl("https://8.8.8.8/start.txt")).isEmpty();
    }

    /**
     * 公网首跳重定向到公网资源时应继续提取文本。
     */
    @Test
    public void extractTextFromUrl_whenRedirectsToPublicTarget_shouldReturnText() throws Exception {
        TestableAiFileServiceImpl service = new TestableAiFileServiceImpl();
        service.stub("https://8.8.8.8/start.txt", StubHttpURLConnection.redirect("https://8.8.8.8/start.txt", HttpURLConnection.HTTP_MOVED_TEMP, "https://1.1.1.1/final.txt"));
        service.stub("https://1.1.1.1/final.txt", StubHttpURLConnection.ok("https://1.1.1.1/final.txt", "text/plain", "hello"));

        assertThat(service.extractTextFromUrl("https://8.8.8.8/start.txt")).isEqualTo("hello");
    }

    /**
     * 相对重定向也应先解析为绝对地址再继续访问。
     */
    @Test
    public void extractTextFromUrl_whenRedirectsRelativePath_shouldResolveAgainstCurrentUrl() throws Exception {
        TestableAiFileServiceImpl service = new TestableAiFileServiceImpl();
        service.stub("https://8.8.8.8/start.txt", StubHttpURLConnection.redirect("https://8.8.8.8/start.txt", HttpURLConnection.HTTP_MOVED_TEMP, "/final.txt"));
        service.stub("https://8.8.8.8/final.txt", StubHttpURLConnection.ok("https://8.8.8.8/final.txt", "text/plain", "world"));

        assertThat(service.extractTextFromUrl("https://8.8.8.8/start.txt")).isEqualTo("world");
    }

    /**
     * 重定向超限时应安全失败。
     */
    @Test
    public void extractTextFromUrl_whenRedirectExceedsLimit_shouldReturnEmpty() throws Exception {
        TestableAiFileServiceImpl service = new TestableAiFileServiceImpl();
        service.stub("https://8.8.8.8/a.txt", StubHttpURLConnection.redirect("https://8.8.8.8/a.txt", HttpURLConnection.HTTP_MOVED_TEMP, "/b.txt"));
        service.stub("https://8.8.8.8/b.txt", StubHttpURLConnection.redirect("https://8.8.8.8/b.txt", HttpURLConnection.HTTP_MOVED_TEMP, "/c.txt"));
        service.stub("https://8.8.8.8/c.txt", StubHttpURLConnection.redirect("https://8.8.8.8/c.txt", HttpURLConnection.HTTP_MOVED_TEMP, "/d.txt"));
        service.stub("https://8.8.8.8/d.txt", StubHttpURLConnection.redirect("https://8.8.8.8/d.txt", HttpURLConnection.HTTP_MOVED_TEMP, "/e.txt"));

        assertThat(service.extractTextFromUrl("https://8.8.8.8/a.txt")).isEmpty();
    }

    /**
     * 可控连接的文件服务测试桩。
     */
    private static class TestableAiFileServiceImpl extends AiFileServiceImpl {
        private final Map<String, StubHttpURLConnection> connections = new HashMap<>();

        /**
         * 注册 URL 对应的连接桩。
         */
        void stub(String url, StubHttpURLConnection connection) {
            connections.put(url, connection);
        }

        /**
         * 返回预设的连接桩。
         */
        @Override
        HttpURLConnection openConnection(URL url) throws IOException {
            StubHttpURLConnection connection = connections.get(url.toString());
            if (connection == null) {
                throw new IOException("missing stub for url: " + url);
            }
            return connection;
        }
    }

    /**
     * HTTP 连接测试桩。
     */
    private static class StubHttpURLConnection extends HttpURLConnection {
        private final int responseCode;
        private final String contentType;
        private final byte[] body;
        private final Map<String, String> headers = new HashMap<>();

        /**
         * 构造测试连接。
         */
        protected StubHttpURLConnection(URL url, int responseCode, String contentType, byte[] body) {
            super(url);
            this.responseCode = responseCode;
            this.contentType = contentType;
            this.body = body == null ? new byte[0] : body;
        }

        /**
         * 构造重定向连接。
         */
        static StubHttpURLConnection redirect(String url, int responseCode, String location) throws Exception {
            StubHttpURLConnection connection = new StubHttpURLConnection(new URL(url), responseCode, null, null);
            connection.headers.put("Location", location);
            return connection;
        }

        /**
         * 构造成功连接。
         */
        static StubHttpURLConnection ok(String url, String contentType, String body) throws Exception {
            return new StubHttpURLConnection(new URL(url), HttpURLConnection.HTTP_OK, contentType, body.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * 断开连接。
         */
        @Override
        public void disconnect() {
        }

        /**
         * 是否使用代理。
         */
        @Override
        public boolean usingProxy() {
            return false;
        }

        /**
         * 建立连接。
         */
        @Override
        public void connect() {
        }

        /**
         * 返回响应码。
         */
        @Override
        public int getResponseCode() {
            return responseCode;
        }

        /**
         * 返回响应头。
         */
        @Override
        public String getHeaderField(String name) {
            return headers.get(name);
        }

        /**
         * 返回内容类型。
         */
        @Override
        public String getContentType() {
            return contentType;
        }

        /**
         * 返回响应体。
         */
        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(body);
        }
    }
}
