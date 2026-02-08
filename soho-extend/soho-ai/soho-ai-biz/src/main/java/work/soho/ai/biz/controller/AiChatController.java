package work.soho.ai.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.service.AiChatService;
import work.soho.common.core.result.R;

@Api(tags = "AI Chat")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin")
public class AiChatController {
    private final AiChatService aiChatService;

    @PostMapping("/chat")
    @ApiOperation(value = "AI chat", notes = "Unified AI chat endpoint")
    public R<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        return R.success(aiChatService.chat(request));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "AI chat stream", notes = "Unified AI chat stream endpoint")
    public Flux<ServerSentEvent<String>> stream(@RequestBody AiChatRequest request) {
        return aiChatService.streamChat(request)
                .map(payload -> ServerSentEvent.builder(payload).build());
    }
}
