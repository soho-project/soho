package work.soho.ai.biz.controller.open;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.ai.biz.dto.AiUserApiKeyCreatedResponse;
import work.soho.ai.biz.request.CreateAiUserApiKeyRequest;
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.common.core.result.R;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.api.annotation.OpenApi;
import work.soho.open.api.annotation.OpenApiDoc;

/**
 * AI 开放接口-用户 API Key
 *
 * @author fang
 */
@Api(tags = "AI 开放接口-用户 API Key")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/open/app/apiKey")
@OpenApiDoc(value = "AI用户API Key", name = "AI用户API Key", description = "AI用户API Key")
public class OpenAiUserApiKeyController {
    private final AiUserApiKeyService aiUserApiKeyService;

    /**
     * 创建 AI API Key
     *
     * @param userDetails 开放平台登录用户
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping
    @ApiOperation("创建 AI API Key")
    @OpenApi(value = "open::ai::apiKey::create", name = "创建 AI API Key")
    @OpenApiDoc(value = "open::ai::apiKey::create", name = "创建 AI API Key", description = "创建 AI API Key", authRole = "openApp")
    public R<AiUserApiKeyCreatedResponse> create(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                 @RequestBody CreateAiUserApiKeyRequest request) {
        if (userDetails == null || userDetails.getId() == null) {
            return R.error("用户未登录");
        }
        if (request == null) {
            return R.error("请求参数不能为空");
        }
        return R.success(aiUserApiKeyService.createKey(userDetails.getId(), request));
    }
}
