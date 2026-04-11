package work.soho.ai.biz.controller.guest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.soho.ai.biz.service.AiOpenApiService;

import java.util.Map;

/**
 * Gemini 兼容接口控制器。
 */
@Log4j2
@Api(tags = "AI Gemini Compatible")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/guest/openai/v1beta")
public class GuestAiGeminiController {
    private static final String CLIENT_ERROR_MESSAGE = "临时错误，如果长期错误请联系管理员";

    private final AiOpenApiService aiOpenApiService;

    /**
     * 查询 Gemini 原生模型列表。
     */
    @GetMapping("/models")
    @ApiOperation("Gemini 兼容 models")
    public Object models(@RequestHeader("Authorization") String authorization) {
        log.info("Gemini 兼容 models");
        try {
            return aiOpenApiService.geminiModels(authorization);
        } catch (RuntimeException ex) {
            log.error("Gemini 兼容 models 失败, msg={}", ex.getMessage(), ex);
            return buildGeminiErrorResponse();
        }
    }

    /**
     * 处理 Gemini 原生 generateContent 请求。
     */
    @PostMapping("/models/{model}:generateContent")
    @ApiOperation("Gemini 兼容 generateContent")
    public Object generateContent(@PathVariable("model") String model,
                                  @RequestParam("key") String key,
                                  @RequestBody(required = false) Map<String, Object> request) {
        log.info("Gemini 兼容 generateContent, model={}", model);
        try {
            return aiOpenApiService.geminiGenerateContent(key, model, request);
        } catch (RuntimeException ex) {
            log.error("Gemini 兼容 generateContent 失败, model={}, msg={}", model, ex.getMessage(), ex);
            return buildGeminiErrorResponse();
        }
    }

    /**
     * 构建 Gemini 兼容错误响应。
     */
    private Object buildGeminiErrorResponse() {
        return java.util.Map.of(
                "error", java.util.Map.of(
                        "message", CLIENT_ERROR_MESSAGE,
                        "code", 500
                )
        );
    }
}
