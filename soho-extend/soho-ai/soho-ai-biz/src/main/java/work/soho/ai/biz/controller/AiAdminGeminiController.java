package work.soho.ai.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.ai.biz.controller.AiClientErrorSupport;
import work.soho.ai.biz.service.AiOpenApiService;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.Map;

/**
 * 管理端 Gemini 兼容接口控制器。
 */
@Log4j2
@Api(tags = "管理端 AI Gemini Compatible")
@RestController
@RequiredArgsConstructor
@RequestMapping({"/ai/admin/openai/v1", "/ai/admin/openai/v1beta"})
public class AiAdminGeminiController {
    private static final String CLIENT_ERROR_MESSAGE = "临时错误，如果长期错误请联系管理员";

    private final AiOpenApiService aiOpenApiService;

    /**
     * 查询 Gemini 原生模型列表。
     *
     * @param userDetails 登录用户
     * @return 模型列表
     */
    @GetMapping("/models")
    @Node(value = "admin::ai::gemini::models", name = "管理端 Gemini 模型列表")
    @ApiOperation("管理端 Gemini 兼容 models")
    public Object models(@AuthenticationPrincipal SohoUserDetails userDetails) {
        log.info("管理端 Gemini 兼容 models");
        try {
            return aiOpenApiService.geminiModelsByUserId(userDetails == null ? null : userDetails.getId());
        } catch (RuntimeException ex) {
            log.error("管理端 Gemini 兼容 models 失败, msg={}", ex.getMessage(), ex);
            return buildGeminiErrorResponse(ex);
        }
    }

    /**
     * 处理 Gemini 原生 generateContent 请求。
     *
     * @param model Gemini 模型名
     * @param userDetails 登录用户
     * @param request 请求体
     * @return Gemini 原生响应
     */
    @PostMapping("/models/{model}:generateContent")
    @Node(value = "admin::ai::gemini::generateContent", name = "管理端 Gemini 生成内容")
    @ApiOperation("管理端 Gemini 兼容 generateContent")
    public Object generateContent(@PathVariable("model") String model,
                                  @AuthenticationPrincipal SohoUserDetails userDetails,
                                  @RequestBody(required = false) Map<String, Object> request) {
        log.info("管理端 Gemini 兼容 generateContent, model={}", model);
        try {
            return aiOpenApiService.geminiGenerateContentByUserId(userDetails == null ? null : userDetails.getId(), model, request);
        } catch (RuntimeException ex) {
            log.error("管理端 Gemini 兼容 generateContent 失败, model={}, msg={}", model, ex.getMessage(), ex);
            return buildGeminiErrorResponse(ex);
        }
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
