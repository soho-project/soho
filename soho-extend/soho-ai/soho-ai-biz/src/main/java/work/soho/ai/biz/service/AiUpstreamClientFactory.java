package work.soho.ai.biz.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.utils.AiProxyLayerUtils;

/**
 * AI 上游客户端工厂。
 */
public interface AiUpstreamClientFactory {
    /**
     * 获取可复用 WebClient。
     */
    WebClient getWebClient(Integer timeoutMs, AiProxyLayerUtils.ProxySettings proxySettings);

    /**
     * 执行 JSON 请求并返回文本响应。
     */
    ResponseEntity<String> exchangeJson(String url,
                                        HttpMethod method,
                                        HttpHeaders headers,
                                        Object body,
                                        Integer timeoutMs,
                                        AiProxyLayerUtils.ProxySettings proxySettings);

    /**
     * 执行二进制请求。
     */
    ResponseEntity<byte[]> exchangeBinary(String url,
                                          HttpMethod method,
                                          HttpHeaders headers,
                                          Object body,
                                          Integer timeoutMs,
                                          AiProxyLayerUtils.ProxySettings proxySettings);

    /**
     * 执行 multipart 请求。
     */
    ResponseEntity<byte[]> exchangeMultipart(String url,
                                             HttpMethod method,
                                             HttpHeaders headers,
                                             Object body,
                                             Integer timeoutMs,
                                             AiProxyLayerUtils.ProxySettings proxySettings);

    /**
     * 执行流式请求并返回文本块流。
     */
    Flux<String> exchangeStream(String url,
                                HttpMethod method,
                                HttpHeaders headers,
                                Object body,
                                Integer timeoutMs,
                                AiProxyLayerUtils.ProxySettings proxySettings);
}
