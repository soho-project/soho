package work.soho.content.biz.wordpress;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/wp-json/wp/v2/")
                .path(path);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(properties.getUsername()) && StringUtils.hasText(properties.getAppPassword())) {
            headers.setBasicAuth(properties.getUsername(), properties.getAppPassword());
        }
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<List<T>> response = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, request, type);
        return response.getBody();
    }
}
