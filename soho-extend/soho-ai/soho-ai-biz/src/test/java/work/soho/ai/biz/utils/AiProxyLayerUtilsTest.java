package work.soho.ai.biz.utils;

import org.junit.Test;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AiProxyLayerUtilsTest {

    @Test
    public void resolve_whenSocksType_shouldBuildSocksProxy() {
        Map<String, Object> config = new HashMap<>();
        config.put("proxyType", "socks5");
        config.put("proxyHost", "127.0.0.1");
        config.put("proxyPort", 7891);

        Proxy proxy = AiProxyLayerUtils.buildJavaProxy(config);
        AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(config);

        assertThat(proxy).isNotNull();
        assertThat(proxy.type()).isEqualTo(Proxy.Type.SOCKS);
        assertThat(settings).isNotNull();
        assertThat(settings.isHttpProxy()).isFalse();
        assertThat(settings.isLocalRelayRequired()).isFalse();
        assertThat(settings.getHost()).isEqualTo("127.0.0.1");
        assertThat(settings.getPort()).isEqualTo(7891);
    }

    @Test
    public void resolve_whenSsType_shouldMapToSocksLocalRelay() {
        Map<String, Object> config = new HashMap<>();
        config.put("proxyType", "ss");
        config.put("proxyHost", "127.0.0.1");
        config.put("proxyPort", 7890);

        Proxy proxy = AiProxyLayerUtils.buildJavaProxy(config);
        AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(config);

        assertThat(proxy).isNotNull();
        assertThat(proxy.type()).isEqualTo(Proxy.Type.SOCKS);
        assertThat(settings).isNotNull();
        assertThat(settings.isHttpProxy()).isFalse();
        assertThat(settings.isLocalRelayRequired()).isTrue();
    }

    @Test
    public void resolve_whenVmessUrl_shouldMapToSocksLocalRelay() {
        Map<String, Object> config = new HashMap<>();
        config.put("proxyUrl", "vmess://127.0.0.1:7890");

        AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(config);
        Proxy proxy = AiProxyLayerUtils.buildJavaProxy(config);

        assertThat(settings).isNotNull();
        assertThat(settings.getHost()).isEqualTo("127.0.0.1");
        assertThat(settings.getPort()).isEqualTo(7890);
        assertThat(settings.isLocalRelayRequired()).isTrue();
        assertThat(settings.getProtocol()).isEqualTo("vmess");
        assertThat(settings.getProxyUrl()).isEqualTo("vmess://127.0.0.1:7890");
        assertThat(proxy).isNotNull();
        assertThat(proxy.type()).isEqualTo(Proxy.Type.SOCKS);
    }

    @Test
    public void resolve_whenVmessBase64Url_shouldBeAccepted() {
        Map<String, Object> config = new HashMap<>();
        config.put("proxyType", "vmess");
        config.put("proxyUrl", "vmess://eyJ2IjoiMiIsInBzIjoiU0ciLCJhZGQiOiJmM2E5azVkOC5nYWJ1aXQuY29tIiwicG9ydCI6IjQ4NjE3IiwiaWQiOiI4RjIzNjExRS03OTBBLTQ3MjItQkI5My1CNzg5NkJGOEVEQUQiLCJhaWQiOiIwIiwic2N5IjoiYXV0byIsIm5ldCI6InRjcCIsInR5cGUiOiJub25lIiwiaG9zdCI6IiIsInBhdGgiOiIiLCJ0bHMiOiIifQ==");

        AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(config);

        assertThat(settings).isNotNull();
        assertThat(settings.isLocalRelayRequired()).isTrue();
        assertThat(settings.getProtocol()).isEqualTo("vmess");
        assertThat(settings.getProxyUrl()).startsWith("vmess://");
    }

    @Test
    public void resolve_whenHysteria2Url_shouldMapToSocksLocalRelay() {
        Map<String, Object> config = new HashMap<>();
        config.put("proxyUrl", "hysteria2://password@relay.example.com:443?sni=relay.example.com&insecure=1");

        AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(config);

        assertThat(settings).isNotNull();
        assertThat(settings.isLocalRelayRequired()).isTrue();
        assertThat(settings.getProtocol()).isEqualTo("hysteria2");
        assertThat(settings.getHost()).isEqualTo("relay.example.com");
        assertThat(settings.getPort()).isEqualTo(443);
    }

    @Test
    public void resolve_whenHttpUrlWithAuth_shouldReadAuth() {
        Map<String, Object> config = new HashMap<>();
        config.put("proxyUrl", "http://demo:pwd@127.0.0.1:8080");

        AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(config);

        assertThat(settings).isNotNull();
        assertThat(settings.isHttpProxy()).isTrue();
        assertThat(settings.getUsername()).isEqualTo("demo");
        assertThat(settings.getPassword()).isEqualTo("pwd");
    }

    @Test
    public void resolve_whenUnsupportedProxyType_shouldThrow() {
        Map<String, Object> config = new HashMap<>();
        config.put("proxyType", "ftp");
        config.put("proxyHost", "127.0.0.1");
        config.put("proxyPort", 8080);

        assertThatThrownBy(() -> AiProxyLayerUtils.resolve(config))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("unsupported proxyType: ftp");
    }
}
