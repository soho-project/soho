package work.soho.ai.biz.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
                log.info("responses 原始请求体: <empty>");
                return;
            }
            String encoding = wrappedRequest.getCharacterEncoding();
            Charset charset = StringUtils.hasText(encoding) ? Charset.forName(encoding) : StandardCharsets.UTF_8;
            String rawBody = new String(content, charset);
            log.info("responses 原始请求体: {}", rawBody);
        }
    }
}
