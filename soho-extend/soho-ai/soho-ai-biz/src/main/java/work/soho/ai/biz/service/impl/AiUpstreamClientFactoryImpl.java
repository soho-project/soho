package work.soho.ai.biz.service.impl;

import io.netty.channel.ChannelOption;
import io.netty.resolver.NoopAddressResolverGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;
import work.soho.ai.biz.service.AiUpstreamClientFactory;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.common.core.util.StringUtils;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AI 上游客户端工厂实现。
 */
@Service
@RequiredArgsConstructor
public class AiUpstreamClientFactoryImpl implements AiUpstreamClientFactory {
    private static final int DEFAULT_TIMEOUT_MS = 60000;

    private final ConnectionProvider aiUpstreamConnectionProvider;
    private final ConcurrentMap<ClientKey, WebClient> webClientCache = new ConcurrentHashMap<>();

    @Override
    public WebClient getWebClient(Integer timeoutMs, AiProxyLayerUtils.ProxySettings proxySettings) {
        ClientKey key = ClientKey.of(timeoutMs, proxySettings);
        return webClientCache.computeIfAbsent(key, this::createWebClient);
    }

    @Override
    public ResponseEntity<String> exchangeJson(String url,
                                               HttpMethod method,
                                               HttpHeaders headers,
                                               Object body,
                                               Integer timeoutMs,
                                               AiProxyLayerUtils.ProxySettings proxySettings) {
        WebClient.RequestHeadersSpec<?> request = prepareRequest(url, method, headers, timeoutMs, proxySettings, body);
        return request.retrieve()
                .toEntity(String.class)
                .block();
    }

    @Override
    public ResponseEntity<byte[]> exchangeBinary(String url,
                                                 HttpMethod method,
                                                 HttpHeaders headers,
                                                 Object body,
                                                 Integer timeoutMs,
                                                 AiProxyLayerUtils.ProxySettings proxySettings) {
        WebClient.RequestHeadersSpec<?> request = prepareRequest(url, method, headers, timeoutMs, proxySettings, body);
        return request.retrieve()
                .toEntity(byte[].class)
                .block();
    }

    @Override
    public ResponseEntity<byte[]> exchangeMultipart(String url,
                                                    HttpMethod method,
                                                    HttpHeaders headers,
                                                    Object body,
                                                    Integer timeoutMs,
                                                    AiProxyLayerUtils.ProxySettings proxySettings) {
        WebClient.RequestBodySpec request = prepareRequestSpec(url, method, headers, timeoutMs, proxySettings);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = body == null
                ? request
                : request.body(BodyInserters.fromMultipartData((org.springframework.util.MultiValueMap<String, ?>) body));
        return requestHeadersSpec.retrieve()
                .toEntity(byte[].class)
                .block();
    }

    private WebClient.RequestHeadersSpec<?> prepareRequest(String url,
                                                           HttpMethod method,
                                                           HttpHeaders headers,
                                                           Integer timeoutMs,
                                                           AiProxyLayerUtils.ProxySettings proxySettings,
                                                           Object body) {
        WebClient.RequestBodySpec request = prepareRequestSpec(url, method, headers, timeoutMs, proxySettings);
        if (body == null) {
            return request;
        }
        return request.bodyValue(body);
    }

    private WebClient.RequestBodySpec prepareRequestSpec(String url,
                                                         HttpMethod method,
                                                         HttpHeaders headers,
                                                         Integer timeoutMs,
                                                         AiProxyLayerUtils.ProxySettings proxySettings) {
        WebClient webClient = getWebClient(timeoutMs, proxySettings);
        WebClient.RequestBodySpec request = webClient.method(method).uri(url);
        if (headers != null && !headers.isEmpty()) {
            request.headers(httpHeaders -> httpHeaders.addAll(headers));
        }
        return request;
    }

    private WebClient createWebClient(ClientKey key) {
        HttpClient httpClient = createHttpClient(key);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private HttpClient createHttpClient(ClientKey key) {
        HttpClient httpClient = HttpClient.create(aiUpstreamConnectionProvider)
                .resolver(NoopAddressResolverGroup.INSTANCE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, key.timeoutMs)
                .responseTimeout(Duration.ofMillis(key.timeoutMs));
        if (key.proxySettings == null) {
            return httpClient;
        }
        return httpClient.proxy(spec -> {
            ProxyProvider.Builder builder;
            if (key.proxySettings.isHttpProxy()) {
                builder = spec.type(ProxyProvider.Proxy.HTTP)
                        .host(key.proxySettings.getHost())
                        .port(key.proxySettings.getPort());
            } else {
                builder = spec.type(ProxyProvider.Proxy.SOCKS5)
                        .host(key.proxySettings.getHost())
                        .port(key.proxySettings.getPort());
            }
            if (StringUtils.isNotBlank(key.proxySettings.getUsername())) {
                builder.username(key.proxySettings.getUsername());
            }
            if (StringUtils.isNotBlank(key.proxySettings.getPassword())) {
                builder.password(ignored -> key.proxySettings.getPassword());
            }
        });
    }

    static final class ClientKey {
        private final int timeoutMs;
        private final AiProxyLayerUtils.ProxySettings proxySettings;
        private final String protocol;
        private final String host;
        private final int port;
        private final String username;
        private final boolean httpProxy;
        private final boolean localRelayRequired;

        private ClientKey(int timeoutMs, AiProxyLayerUtils.ProxySettings proxySettings) {
            this.timeoutMs = timeoutMs;
            this.proxySettings = proxySettings;
            this.protocol = proxySettings == null ? "" : safe(proxySettings.getProtocol());
            this.host = proxySettings == null ? "" : safe(proxySettings.getHost());
            this.port = proxySettings == null ? 0 : proxySettings.getPort();
            this.username = proxySettings == null ? "" : safe(proxySettings.getUsername());
            this.httpProxy = proxySettings != null && proxySettings.isHttpProxy();
            this.localRelayRequired = proxySettings != null && proxySettings.isLocalRelayRequired();
        }

        static ClientKey of(Integer timeoutMs, AiProxyLayerUtils.ProxySettings proxySettings) {
            int normalizedTimeout = timeoutMs == null || timeoutMs <= 0 ? DEFAULT_TIMEOUT_MS : timeoutMs;
            return new ClientKey(normalizedTimeout, proxySettings);
        }

        private static String safe(String value) {
            return value == null ? "" : value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ClientKey)) {
                return false;
            }
            ClientKey clientKey = (ClientKey) o;
            return timeoutMs == clientKey.timeoutMs
                    && port == clientKey.port
                    && httpProxy == clientKey.httpProxy
                    && localRelayRequired == clientKey.localRelayRequired
                    && Objects.equals(protocol, clientKey.protocol)
                    && Objects.equals(host, clientKey.host)
                    && Objects.equals(username, clientKey.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(timeoutMs, protocol, host, port, username, httpProxy, localRelayRequired);
        }
    }
}
