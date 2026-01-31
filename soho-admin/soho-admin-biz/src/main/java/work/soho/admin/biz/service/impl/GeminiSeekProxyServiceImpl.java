package work.soho.admin.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import work.soho.admin.api.request.AiChatRequest;
import work.soho.admin.biz.config.GeminiProperties;
import work.soho.admin.biz.service.AiProxyService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理 Google Gemini API
 *
 * @doc https://ai.google.dev/gemini-api/docs/get-started/rest
 */
@Service
@RequiredArgsConstructor
public class GeminiSeekProxyServiceImpl implements AiProxyService {
    private final WebClient geminiWebClient;
    private final GeminiProperties props;
    private static final String SYSTEM_PROMPT = "你是一个专业写作与开发文档助手，输出尽量结构化、可直接使用。";

    @Override
    public Flux<String> streamChat(AiChatRequest req) {
        Map<String, Object> body = buildGeminiBody(req);

        return geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/{model}:streamGenerateContent")
                        .queryParam("alt", "sse")
                        .build(props.getModel()))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(buf -> {
                    byte[] bytes = new byte[buf.readableByteCount()];
                    buf.read(bytes);
                    DataBufferUtils.release(buf);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .transform(this::sseToPayloadFlux); // 输出 "{...}"
    }

    private Map<String, Object> buildGeminiBody(AiChatRequest req) {
        String userContent = buildUserContent(req);

        Map<String, Object> body = new LinkedHashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userContent))
        ));

        body.put("contents", contents);
        body.put("system_instruction", Map.of(
                "parts", List.of(Map.of("text", StringUtils.isEmpty(req.getSystemPrompt()) ? SYSTEM_PROMPT : req.getSystemPrompt()))
        ));
        body.put("generationConfig", Map.of(
                "temperature", 0.7
        ));
        return body;
    }

    private String buildUserContent(AiChatRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("mode=").append(nullToEmpty(req.getMode())).append("\n");
        sb.append("lang=").append(nullToEmpty(req.getLang())).append("\n");
        if (StringUtils.hasText(req.getPrompt())) {
            sb.append("prompt=").append(req.getPrompt()).append("\n");
        }
        if (StringUtils.hasText(req.getSelectedText())) {
            sb.append("\n【选中文本】\n").append(req.getSelectedText()).append("\n");
        }
        if (StringUtils.hasText(req.getFullText())) {
            String full = req.getFullText();
            int max = 8000;
            if (full.length() > max) full = full.substring(0, max);
            sb.append("\n【全文参考(截断)】\n").append(full).append("\n");
        }
        sb.append("\n请直接输出最终结果，不要解释过程。");
        return sb.toString();
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /**
     * 把 Gemini SSE 文本流（可能被随意拆包）拼成事件，再提取 data payload
     */
    private Flux<String> sseToPayloadFlux(Flux<String> rawTextFlux) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();

            rawTextFlux.subscribe(
                    part -> {
                        if (part == null) return;
                        buffer.append(part);

                        while (true) {
                            DelimiterHit hit = findEventDelimiter(buffer);
                            if (hit.index < 0) break;
                            String event = buffer.substring(0, hit.index);
                            buffer.delete(0, hit.index + hit.length);
                            emitDataLines(event, sink);
                        }
                    },
                    sink::error,
                    () -> {
                        if (buffer.length() > 0) {
                            emitDataLines(buffer.toString(), sink);
                        }
                        sink.complete();
                    }
            );
        });
    }

    private List<String> extractDataLines(String chunk) {
        if (!StringUtils.hasText(chunk)) return List.of();

        String[] lines = chunk.split("\\r?\\n");
        List<String> out = new ArrayList<>();

        for (String line : lines) {
            if (!StringUtils.hasText(line)) continue;
            line = line.trim();
            if (line.startsWith(":")) continue;
            if (line.startsWith("data:")) {
                String data = line.substring(5).trim();
                if (StringUtils.hasText(data)) {
                    out.add(data);
                }
            }
        }
        return out;
    }

    private void emitDataLines(String event, reactor.core.publisher.FluxSink<String> sink) {
        List<String> dataLines = extractDataLines(event);
        for (String dataLine : dataLines) {
            sink.next(dataLine);
        }
    }

    private DelimiterHit findEventDelimiter(StringBuilder buffer) {
        int lfIdx = buffer.indexOf("\n\n");
        int crlfIdx = buffer.indexOf("\r\n\r\n");

        if (lfIdx < 0 && crlfIdx < 0) return new DelimiterHit(-1, 0);
        if (lfIdx < 0) return new DelimiterHit(crlfIdx, 4);
        if (crlfIdx < 0) return new DelimiterHit(lfIdx, 2);
        return (crlfIdx < lfIdx) ? new DelimiterHit(crlfIdx, 4) : new DelimiterHit(lfIdx, 2);
    }

    private static final class DelimiterHit {
        final int index;
        final int length;

        DelimiterHit(int index, int length) {
            this.index = index;
            this.length = length;
        }
    }
}
