package work.soho.admin.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import work.soho.admin.api.request.AiChatRequest;
import work.soho.admin.biz.service.impl.DeepSeekProxyServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/admin/adminAi")
public class AdminAiController {
    private final DeepSeekProxyServiceImpl proxyService;

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream(@RequestBody AiChatRequest req) {
        return proxyService.streamChat(req) // 这里必须是 "{...}" 或 "[DONE]"
                .map(payload -> ServerSentEvent.builder(payload).build());
    }
}
