package work.soho.ai.biz.controller.guest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;
import work.soho.ai.biz.service.AiOpenApiService;
import work.soho.ai.biz.controller.AiClientErrorSupport;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Api(tags = "AI OpenAI Compatible")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/guest/openai/v1")
public class GuestAiOpenAiController {
    private static final String CLIENT_ERROR_MESSAGE = "临时错误，如果长期错误请联系管理员";
    private final AiOpenApiService aiOpenApiService;

    /**
     * 查询 OpenAI/Codex 兼容余额。
     */
    @GetMapping(value = "/user/balance")
    @ApiOperation("OpenAI/Codex 兼容余额查询")
    public Object balance(@RequestHeader("Authorization") String authorization,
                          @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        log.info("OpenAI/Codex 兼容余额查询, userAgent={}", userAgent);
        try {
            return aiOpenApiService.balance(authorization);
        } catch (RuntimeException ex) {
            log.error("OpenAI/Codex 兼容余额查询失败, msg={}", ex.getMessage(), ex);
            return buildBalanceErrorResponse();
        }
    }

    /**
     * 查询兼容客户端的用户套餐用量。
     */
    @GetMapping(value = "/api/user/self")
    @ApiOperation("OpenAI/Codex 兼容用户套餐用量查询")
    public Object self(@AuthenticationPrincipal SohoUserDetails userDetails,
                       @RequestHeader(value = "New-Api-User", required = false) String newApiUserHeader) {
        log.info("OpenAI/Codex 兼容套餐查询, userId={}, newApiUser={}",
                userDetails == null ? null : userDetails.getId(), newApiUserHeader);
        try {
            return aiOpenApiService.selfPackage(userDetails == null ? null : userDetails.getId(), newApiUserHeader);
        } catch (RuntimeException ex) {
            log.error("OpenAI/Codex 兼容套餐查询失败, msg={}", ex.getMessage(), ex);
            return buildSelfPackageErrorResponse(ex);
        }
    }

    /**
     * 查询 OpenAI 兼容模型列表。
     */
    @GetMapping(value = "/models")
    @ApiOperation("OpenAI 兼容 models")
    public Object models(@RequestHeader("Authorization") String authorization) {
        log.info("OpenAI 兼容 models");
        return aiOpenApiService.models(authorization);
    }

    /**
     * 处理 OpenAI 兼容 chat completions 请求。
     * 发生异常时统一返回脱敏后的错误结构，避免直接透出上游错误详情。
     */
    @PostMapping(value = "/chat/completions")
    @ApiOperation("OpenAI 兼容 chat completions")
    public Object chatCompletions(@RequestHeader("Authorization") String authorization,
                                  @RequestBody OpenAiChatCompletionRequest request) {
        log.info("OpenAI 兼容 chat completions 请求摘要: {}", JacksonUtils.toJson(buildChatCompletionsLogSummary(request)));
        try {
            if (Boolean.TRUE.equals(request.getStream())) {
                SseEmitter emitter = new SseEmitter(0L);
                subscribeWithEmitter(emitter, aiOpenApiService.streamChatCompletions(authorization, request));
                return emitter;
            }
            return aiOpenApiService.chatCompletions(authorization, request);
        } catch (RuntimeException ex) {
            log.error("OpenAI 兼容 chat completions 失败, msg={}", ex.getMessage(), ex);
            return buildOpenAiErrorResponse(ex);
        }
    }

    /**
     * 处理 OpenAI 兼容 responses 请求。
     * 发生异常时统一返回脱敏后的错误结构，避免直接透出上游错误详情。
     */
    @PostMapping(value = "/responses")
    @ApiOperation("OpenAI 兼容 responses")
    public Object responses(@RequestHeader("Authorization") String authorization,
                            @RequestBody OpenAiResponsesRequest request) {
        log.info("OpenAI 兼容 responses 请求摘要: {}", JacksonUtils.toJson(buildResponsesLogSummary(request)));
        try {
            if (Boolean.TRUE.equals(request.getStream())) {
                SseEmitter emitter = new SseEmitter(0L);
                subscribeWithEmitter(emitter, aiOpenApiService.streamResponses(authorization, request));
                return emitter;
            }
            return aiOpenApiService.responses(authorization, request);
        } catch (RuntimeException ex) {
            log.error("OpenAI 兼容 responses 失败, msg={}", ex.getMessage(), ex);
            return buildOpenAiErrorResponse(ex);
        }
    }

    /**
     * 处理 OpenAI 兼容 images generations 请求。
     */
    @PostMapping(value = "/images/generations")
    @ApiOperation("OpenAI 兼容 images generations")
    public Object imageGenerations(@RequestHeader("Authorization") String authorization,
                                   @RequestBody Map<String, Object> request) {
        log.info("OpenAI 兼容 images generations 请求摘要: {}", JacksonUtils.toJson(buildSimpleLogSummary(request)));
        try {
            return aiOpenApiService.imageGenerations(authorization, request);
        } catch (RuntimeException ex) {
            log.error("OpenAI 兼容 images generations 失败, msg={}", ex.getMessage(), ex);
            return buildOpenAiErrorResponse(ex);
        }
    }

    /**
     * 处理 OpenAI 兼容 embeddings 请求。
     */
    @PostMapping(value = "/embeddings")
    @ApiOperation("OpenAI 兼容 embeddings")
    public Object embeddings(@RequestHeader("Authorization") String authorization,
                             @RequestBody Map<String, Object> request) {
        log.info("OpenAI 兼容 embeddings 请求摘要: {}", JacksonUtils.toJson(buildSimpleLogSummary(request)));
        try {
            return aiOpenApiService.embeddings(authorization, request);
        } catch (RuntimeException ex) {
            log.error("OpenAI 兼容 embeddings 失败, msg={}", ex.getMessage(), ex);
            return buildOpenAiErrorResponse(ex);
        }
    }

    /**
     * 处理 OpenAI 兼容 audio transcriptions 请求。
     */
    @PostMapping(value = "/audio/transcriptions")
    @ApiOperation("OpenAI 兼容 audio transcriptions")
    public Object audioTranscriptions(@RequestHeader("Authorization") String authorization,
                                      @RequestParam Map<String, String> request,
                                      @RequestPart("file") MultipartFile file) {
        log.info("OpenAI 兼容 audio transcriptions 请求摘要: {}", JacksonUtils.toJson(buildMultipartLogSummary(request, file)));
        try {
            return aiOpenApiService.audioTranscriptions(authorization, request, file);
        } catch (RuntimeException ex) {
            log.error("OpenAI 兼容 audio transcriptions 失败, msg={}", ex.getMessage(), ex);
            return buildOpenAiErrorResponse(ex);
        }
    }

    /**
     * 处理 OpenAI 兼容 audio translations 请求。
     */
    @PostMapping(value = "/audio/translations")
    @ApiOperation("OpenAI 兼容 audio translations")
    public Object audioTranslations(@RequestHeader("Authorization") String authorization,
                                    @RequestParam Map<String, String> request,
                                    @RequestPart("file") MultipartFile file) {
        log.info("OpenAI 兼容 audio translations 请求摘要: {}", JacksonUtils.toJson(buildMultipartLogSummary(request, file)));
        try {
            return aiOpenApiService.audioTranslations(authorization, request, file);
        } catch (RuntimeException ex) {
            log.error("OpenAI 兼容 audio translations 失败, msg={}", ex.getMessage(), ex);
            return buildOpenAiErrorResponse(ex);
        }
    }

    /**
     * 处理 OpenAI 兼容 audio speech 请求。
     */
    @PostMapping(value = "/audio/speech")
    @ApiOperation("OpenAI 兼容 audio speech")
    public ResponseEntity<byte[]> audioSpeech(@RequestHeader("Authorization") String authorization,
                                              @RequestBody Map<String, Object> request) {
        log.info("OpenAI 兼容 audio speech 请求摘要: {}", JacksonUtils.toJson(buildSimpleLogSummary(request)));
        try {
            return aiOpenApiService.audioSpeech(authorization, request);
        } catch (RuntimeException ex) {
            log.error("OpenAI 兼容 audio speech 失败, msg={}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    private void sendEvent(SseEmitter emitter, String payload) {
        try {
            emitter.send(SseEmitter.event().data(payload));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private int sizeOf(List<?> list) {
        return list == null ? 0 : list.size();
    }

    Map<String, Object> buildChatCompletionsLogSummary(OpenAiChatCompletionRequest request) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("model", request == null ? null : request.getModel());
        summary.put("stream", request != null && Boolean.TRUE.equals(request.getStream()));
        summary.put("messageCount", request == null || request.getMessages() == null ? 0 : request.getMessages().size());
        return summary;
    }

    Map<String, Object> buildResponsesLogSummary(OpenAiResponsesRequest request) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("model", request == null ? null : request.getModel());
        summary.put("stream", request != null && Boolean.TRUE.equals(request.getStream()));
        summary.put("includeCount", sizeOf(request == null ? null : request.getInclude()));
        summary.put("toolsCount", sizeOf(request == null ? null : request.getTools()));
        summary.put("hasInput", request != null && request.getInput() != null);
        return summary;
    }

    Map<String, Object> buildSimpleLogSummary(Map<String, Object> request) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("model", request == null ? null : request.get("model"));
        summary.put("size", request == null ? 0 : request.size());
        return summary;
    }

    Map<String, Object> buildMultipartLogSummary(Map<String, String> request, MultipartFile file) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("model", request == null ? null : request.get("model"));
        summary.put("fileName", file == null ? null : file.getOriginalFilename());
        summary.put("fileSize", file == null ? 0 : file.getSize());
        summary.put("size", request == null ? 0 : request.size());
        return summary;
    }

    private void subscribeWithEmitter(SseEmitter emitter, Flux<String> flux) {
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        Disposable disposable = flux.subscribe(payload -> sendEvent(emitter, payload),
                ex -> {
                    log.error("OpenAI SSE 请求失败, msg={}", ex.getMessage(), ex);
                    sendEvent(emitter, JacksonUtils.toJson(buildOpenAiErrorResponse(ex instanceof RuntimeException
                            ? (RuntimeException) ex
                            : new RuntimeException(ex))));
                    emitter.complete();
                },
                emitter::complete);
        disposableRef.set(disposable);
        emitter.onCompletion(() -> dispose(disposableRef));
        emitter.onTimeout(() -> {
            dispose(disposableRef);
            emitter.complete();
        });
        emitter.onError(ex -> dispose(disposableRef));
    }

    private void dispose(AtomicReference<Disposable> disposableRef) {
        Disposable disposable = disposableRef.getAndSet(null);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 构建余额查询失败时的兜底响应，保证 Codex 余额提取器可继续工作。
     */
    private Map<String, Object> buildBalanceErrorResponse() {
        Map<String, Object> result = new HashMap<>();
        result.put("object", "balance");
        result.put("is_active", false);
        result.put("balance", 0);
        result.put("unit", "USD");
        return result;
    }

    /**
     * 构建套餐查询失败兜底响应。
     */
    private Map<String, Object> buildSelfPackageErrorResponse(RuntimeException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", AiClientErrorSupport.resolveClientMessage(ex, CLIENT_ERROR_MESSAGE));
        return result;
    }

    /**
     * 构建 OpenAI 兼容错误响应，避免向客户端暴露上游原始错误信息。
     */
    private Map<String, Object> buildOpenAiErrorResponse(RuntimeException ex) {
        String message = AiClientErrorSupport.resolveClientMessage(ex, CLIENT_ERROR_MESSAGE);
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("type", "server_error");
        error.put("code", "server_error");
        Map<String, Object> result = new HashMap<>();
        result.put("error", error);
        return result;
    }
}
