package work.soho.ai.biz.controller.user;

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
import org.springframework.web.bind.annotation.RestController;
import work.soho.ai.biz.controller.AiClientErrorSupport;
import work.soho.ai.biz.service.AiOpenApiService;
import work.soho.common.security.annotation.Node;

import java.util.Map;

/**
 * 用户侧 Gemini 兼容接口控制器。
 */
@Log4j2
@Api(tags = "用户 AI Gemini Compatible")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/user/openai/v1beta")
public class UserAiGeminiController {
    private static final String CLIENT_ERROR_MESSAGE = "临时错误，如果长期错误请联系管理员";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AiOpenApiService aiOpenApiService;

    /**
     * 查询 Gemini 原生模型列表。
     *
     * @param authorization 平台 API Key（Bearer）
     * @return 模型列表
     */
    @GetMapping("/models")
    @Node(value = "user::ai::gemini::models", name = "用户 Gemini 模型列表")
    @ApiOperation("用户 Gemini 兼容 models")
    public Object models(@RequestHeader("Authorization") String authorization) {
        log.info("用户 Gemini 兼容 models");
        try {
            return aiOpenApiService.geminiModels(authorization);
        } catch (RuntimeException ex) {
            log.error("用户 Gemini 兼容 models 失败, msg={}", ex.getMessage(), ex);
            return buildGeminiErrorResponse(ex);
        }
    }

    /**
     * 处理 Gemini 原生 generateContent 请求。
     *
     * @param model Gemini 模型名
     * @param authorization 平台 API Key（Bearer）
     * @param request 请求体
     * @return Gemini 原生响应
     */
    @PostMapping("/models/{model}:generateContent")
    @Node(value = "user::ai::gemini::generateContent", name = "用户 Gemini 生成内容")
    @ApiOperation("用户 Gemini 兼容 generateContent")
    public Object generateContent(@PathVariable("model") String model,
                                  @RequestHeader("Authorization") String authorization,
                                  @RequestBody(required = false) Map<String, Object> request) {
        log.info("用户 Gemini 兼容 generateContent, model={}", model);
        try {
            return aiOpenApiService.geminiGenerateContent(extractBearerToken(authorization), model, request);
        } catch (RuntimeException ex) {
            log.error("用户 Gemini 兼容 generateContent 失败, model={}, msg={}", model, ex.getMessage(), ex);
            return buildGeminiErrorResponse(ex);
        }
    }

    /**
     * 提取 Bearer token。
     *
     * @param authorization Authorization 头
     * @return 纯 token
     */
    private String extractBearerToken(String authorization) {
        if (authorization == null) {
            return "";
        }
        if (authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length()).trim();
        }
        return authorization.trim();
    }

    /**
     * 构建 Gemini 兼容错误响应。
     *
     * @return 脱敏错误信息
     */
    private Map<String, Object> buildGeminiErrorResponse(RuntimeException ex) {
        String message = AiClientErrorSupport.resolveClientMessage(ex, CLIENT_ERROR_MESSAGE);
        return Map.of(
                "error", Map.of(
                        "message", message,
                        "code", 500
                )
        );
    }
}
