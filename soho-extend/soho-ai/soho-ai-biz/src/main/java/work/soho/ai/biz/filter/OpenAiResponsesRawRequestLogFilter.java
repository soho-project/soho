package work.soho.ai.biz.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import work.soho.common.core.util.JacksonUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class OpenAiResponsesRawRequestLogFilter extends OncePerRequestFilter {
    private static final String RESPONSES_PATH = "/ai/guest/openai/v1/responses";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        return !PATH_MATCHER.match(RESPONSES_PATH, uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = request instanceof ContentCachingRequestWrapper
                ? (ContentCachingRequestWrapper) request
                : new ContentCachingRequestWrapper(request);
        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            byte[] content = wrappedRequest.getContentAsByteArray();
            if (content.length == 0) {
                log.info("responses 请求摘要: uri={}, method={}, bodyBytes=0", request.getRequestURI(), request.getMethod());
                return;
            }
            String encoding = wrappedRequest.getCharacterEncoding();
            Charset charset = StringUtils.hasText(encoding) ? Charset.forName(encoding) : StandardCharsets.UTF_8;
            String rawBody = new String(content, charset);
            logSummary(request, content.length, rawBody);
        }
    }

    @SuppressWarnings("unchecked")
    private void logSummary(HttpServletRequest request, int bodyBytes, String rawBody) {
        try {
            Map<String, Object> root = JacksonUtils.toBean(rawBody, Map.class);
            if (root == null) {
                log.info("responses 请求摘要: uri={}, method={}, bodyBytes={}, parseable=false",
                        request.getRequestURI(), request.getMethod(), bodyBytes);
                return;
            }
            Object include = root.get("include");
            Object tools = root.get("tools");
            log.info("responses 请求摘要: uri={}, method={}, bodyBytes={}, parseable=true, model={}, stream={}, hasInput={}, includeCount={}, toolsCount={}",
                    request.getRequestURI(),
                    request.getMethod(),
                    bodyBytes,
                    root.get("model"),
                    asBoolean(root.get("stream")),
                    root.get("input") != null,
                    include instanceof List ? ((List<?>) include).size() : 0,
                    tools instanceof List ? ((List<?>) tools).size() : 0);
        } catch (Exception ex) {
            log.info("responses 请求摘要: uri={}, method={}, bodyBytes={}, parseable=false",
                    request.getRequestURI(), request.getMethod(), bodyBytes);
        }
    }

    private boolean asBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return value != null && Boolean.parseBoolean(String.valueOf(value));
    }
}
