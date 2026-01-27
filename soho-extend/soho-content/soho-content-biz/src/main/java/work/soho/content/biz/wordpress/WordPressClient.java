package work.soho.content.biz.wordpress;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.UnknownContentTypeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import work.soho.content.biz.wordpress.dto.WordPressCommentDto;
import work.soho.content.biz.wordpress.dto.WordPressMediaDto;
import work.soho.content.biz.wordpress.dto.WordPressPostDto;
import work.soho.content.biz.wordpress.dto.WordPressTermDto;
import work.soho.content.biz.wordpress.dto.WordPressUserDto;

import java.util.List;
import java.util.Map;

/**
 * WordPress 接口客户端
 */
@Component
public class WordPressClient {
    private final WordPressProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public WordPressClient(WordPressProperties properties) {
        this.properties = properties;
    }

    public List<WordPressPostDto> getPosts(String type, int page, int perPage) {
        String endpoint = "posts";
        if ("page".equalsIgnoreCase(type) || "pages".equalsIgnoreCase(type)) {
            endpoint = "pages";
        }
        return getList(endpoint, Map.of("page", String.valueOf(page), "per_page", String.valueOf(perPage)),
                new ParameterizedTypeReference<List<WordPressPostDto>>() {});
    }

    public List<WordPressTermDto> getCategories(int page, int perPage) {
        return getList("categories", Map.of("page", String.valueOf(page), "per_page", String.valueOf(perPage)),
                new ParameterizedTypeReference<List<WordPressTermDto>>() {});
    }

    public List<WordPressTermDto> getTags(int page, int perPage) {
        return getList("tags", Map.of("page", String.valueOf(page), "per_page", String.valueOf(perPage)),
                new ParameterizedTypeReference<List<WordPressTermDto>>() {});
    }

    public List<WordPressMediaDto> getMedia(int page, int perPage) {
        return getList("media", Map.of("page", String.valueOf(page), "per_page", String.valueOf(perPage)),
                new ParameterizedTypeReference<List<WordPressMediaDto>>() {});
    }

    public List<WordPressUserDto> getUsers(int page, int perPage) {
        return getList("users", Map.of("page", String.valueOf(page), "per_page", String.valueOf(perPage)),
                new ParameterizedTypeReference<List<WordPressUserDto>>() {});
    }

    public List<WordPressCommentDto> getComments(int page, int perPage) {
        return getList("comments", Map.of("page", String.valueOf(page), "per_page", String.valueOf(perPage)),
                new ParameterizedTypeReference<List<WordPressCommentDto>>() {});
    }

    private <T> List<T> getList(String path, Map<String, String> params, ParameterizedTypeReference<List<T>> type) {
        String baseUrl = properties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("WordPress baseUrl is not configured");
        }
        String restBaseUrl = resolveRestBaseUrl(baseUrl);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restBaseUrl)
                .pathSegment(path);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        String url = builder.toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (StringUtils.hasText(properties.getUsername()) && StringUtils.hasText(properties.getAppPassword())) {
            headers.setBasicAuth(properties.getUsername(), properties.getAppPassword());
        }
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<List<T>> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, type);
            return response.getBody();
        } catch (UnknownContentTypeException e) {
            // 目标接口返回了 HTML（常见于登录页/错误页/被代理拦截），给出更清晰的诊断信息
            String bodySnippet = null;
            try {
                ResponseEntity<String> raw = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (raw.getBody() != null) {
                    String body = raw.getBody().replaceAll("\\s+", " ").trim();
                    bodySnippet = body.length() > 200 ? body.substring(0, 200) + "..." : body;
                }
            } catch (Exception ignored) {
                // ignore secondary failures, we'll still throw a clearer error below
            }
            throw new IllegalStateException("WordPress REST API did not return JSON. url=" + url
                    + (bodySnippet != null ? ", body=" + bodySnippet : ""), e);
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            String bodySnippet = null;
            if (body != null) {
                String normalized = body.replaceAll("\\s+", " ").trim();
                bodySnippet = normalized.length() > 200 ? normalized.substring(0, 200) + "..." : normalized;
            }
            throw new IllegalStateException("WordPress REST API error. status=" + e.getStatusCode()
                    + ", url=" + url + (bodySnippet != null ? ", body=" + bodySnippet : ""), e);
        }
    }

    /**
     * 兼容两种传参方式：
     * 1) 站点根地址：https://example.com
     * 2) REST 基础地址：https://example.com/wp-json/wp/v2
     */
    private String resolveRestBaseUrl(String configuredBaseUrl) {
        String base = configuredBaseUrl.trim();
        // 已经是 REST 基础地址（或至少包含 wp-json），则不再重复拼接
        if (base.contains("/wp-json/")) {
            return stripTrailingSlash(base);
        }
        return stripTrailingSlash(base) + "/wp-json/wp/v2";
    }

    private String stripTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
