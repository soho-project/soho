package work.soho.ai.biz.controller.guest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;
import work.soho.ai.biz.service.AiOpenApiService;
import work.soho.common.core.util.JacksonUtils;

import java.io.IOException;

@Log4j2
@Api(tags = "AI OpenAI Compatible")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/guest/openai/v1")
public class GuestAiOpenAiController {
    private final AiOpenApiService aiOpenApiService;

    @GetMapping(value = "/models")
    @ApiOperation("OpenAI 兼容 models")
    public Object models(@RequestHeader("Authorization") String authorization) {
        log.info("OpenAI 兼容 models");
        return aiOpenApiService.models(authorization);
    }

    @PostMapping(value = "/chat/completions")
    @ApiOperation("OpenAI 兼容 chat completions")
    public Object chatCompletions(@RequestHeader("Authorization") String authorization,
                                  @RequestBody OpenAiChatCompletionRequest request) {
        log.info("OpenAI 兼容 chat completions: {}",  request);
        if (Boolean.TRUE.equals(request.getStream())) {
            SseEmitter emitter = new SseEmitter(0L);
            aiOpenApiService.streamChatCompletions(authorization, request)
                    .subscribe(payload -> sendEvent(emitter, payload),
                            emitter::completeWithError,
                            emitter::complete);
            return emitter;
        }
        return aiOpenApiService.chatCompletions(authorization, request);
    }

    @PostMapping(value = "/responses")
    @ApiOperation("OpenAI 兼容 responses")
    public Object responses(@RequestHeader("Authorization") String authorization,
                            @RequestBody OpenAiResponsesRequest request) {
        log.info("OpenAI 兼容 responses 请求体: {}", JacksonUtils.toJson(request));
        if (Boolean.TRUE.equals(request.getStream())) {
            SseEmitter emitter = new SseEmitter(0L);
            aiOpenApiService.streamResponses(authorization, request)
                    .subscribe(payload -> sendEvent(emitter, payload),
                            emitter::completeWithError,
                            emitter::complete);
            return emitter;
        }
        return aiOpenApiService.responses(authorization, request);
    }

    private void sendEvent(SseEmitter emitter, String payload) {
        try {
            emitter.send(SseEmitter.event().data(payload));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}
