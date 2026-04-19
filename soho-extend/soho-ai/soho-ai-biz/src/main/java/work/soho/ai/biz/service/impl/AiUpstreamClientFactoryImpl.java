package work.soho.ai.biz.service.impl;

import io.netty.channel.ChannelOption;
import io.netty.resolver.NoopAddressResolverGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;
import work.soho.ai.biz.service.AiUpstreamClientFactory;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.common.core.util.StringUtils;

import java.nio.charset.StandardCharsets;
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

    /**
     * 获取可复用 WebClient。
     */
    @Override
    public WebClient getWebClient(Integer timeoutMs, AiProxyLayerUtils.ProxySettings proxySettings) {
        ClientKey key = ClientKey.of(timeoutMs, proxySettings);
        return webClientCache.computeIfAbsent(key, this::createWebClient);
    }

    /**
     * 执行 JSON 请求并返回文本响应。
     */
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

    /**
     * 执行二进制请求。
     */
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

    /**
     * 执行 multipart 请求。
     */
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

    /**
     * 执行流式请求并返回文本块流。
     */
    @Override
    public Flux<String> exchangeStream(String url,
                                       HttpMethod method,
                                       HttpHeaders headers,
                                       Object body,
                                       Integer timeoutMs,
                                       AiProxyLayerUtils.ProxySettings proxySettings) {
        WebClient.RequestHeadersSpec<?> request = prepareRequest(url, method, headers, timeoutMs, proxySettings, body);
        return request.retrieve()
                .onStatus(HttpStatus::isError, response -> buildUpstreamHttpError(url, response))
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString);
    }

    /**
     * 构建通用请求对象。
     */
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

    /**
     * 构建请求头与目标地址。
     */
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

    /**
     * 构建上游 HTTP 异常。
     */
    private Mono<? extends Throwable> buildUpstreamHttpError(String url, ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(errorBody -> Mono.error(new IllegalArgumentException(
                        "upstream api request failed: status=" + response.statusCode().value()
                                + ", url=" + url
                                + ", body=" + errorBody)));
    }

    /**
     * 创建 WebClient。
     */
    private WebClient createWebClient(ClientKey key) {
        HttpClient httpClient = createHttpClient(key);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * 创建底层 HttpClient。
     */
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

    /**
     * 将 DataBuffer 解码为 UTF-8 文本。
     */
    private String bufferToString(DataBuffer buffer) {
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        DataBufferUtils.release(buffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 客户端缓存键。
     */
    static final class ClientKey {
        private final int timeoutMs;
        private final AiProxyLayerUtils.ProxySettings proxySettings;
        private final String protocol;
        private final String host;
        private final int port;
        private final String username;
        private final boolean httpProxy;
        private final boolean localRelayRequired;

        /**
         * 构造缓存键。
         */
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

        /**
         * 规范化入参并创建缓存键。
         */
        static ClientKey of(Integer timeoutMs, AiProxyLayerUtils.ProxySettings proxySettings) {
            int normalizedTimeout = timeoutMs == null || timeoutMs <= 0 ? DEFAULT_TIMEOUT_MS : timeoutMs;
            return new ClientKey(normalizedTimeout, proxySettings);
        }

        /**
         * 兜底空字符串。
         */
        private static String safe(String value) {
            return value == null ? "" : value;
        }

        /**
         * 比较缓存键是否相等。
         */
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

        /**
         * 计算缓存键哈希。
         */
        @Override
        public int hashCode() {
            return Objects.hash(timeoutMs, protocol, host, port, username, httpProxy, localRelayRequired);
        }
    }
}
