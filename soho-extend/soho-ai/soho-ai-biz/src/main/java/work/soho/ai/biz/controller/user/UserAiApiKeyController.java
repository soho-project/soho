package work.soho.ai.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiUserApiKeyCreatedResponse;
import work.soho.ai.biz.dto.AiUserApiKeyView;
import work.soho.ai.biz.request.CreateAiUserApiKeyRequest;
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "用户 AI API Key")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/user/apiKey")
public class UserAiApiKeyController {
    private final AiUserApiKeyService aiUserApiKeyService;

    @GetMapping("/list")
    @Node(value = "user::ai::apiKey::list", name = "获取 AI API Key 列表")
    @ApiOperation("获取 AI API Key 列表")
    public R<List<AiUserApiKeyView>> list(@AuthenticationPrincipal SohoUserDetails userDetails) {
        List<AiUserApiKeyView> list = aiUserApiKeyService.list(new LambdaQueryWrapper<AiUserApiKey>()
                        .eq(AiUserApiKey::getUserId, userDetails.getId())
                        .orderByDesc(AiUserApiKey::getId))
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
        return R.success(list);
    }

    @PostMapping
    @Node(value = "user::ai::apiKey::add", name = "创建 AI API Key")
    @ApiOperation("创建 AI API Key")
    public R<AiUserApiKeyCreatedResponse> create(@AuthenticationPrincipal SohoUserDetails userDetails,
                                                 @RequestBody CreateAiUserApiKeyRequest request) {
        return R.success(aiUserApiKeyService.createKey(userDetails.getId(), request));
    }

    @DeleteMapping("/{id}")
    @Node(value = "user::ai::apiKey::remove", name = "停用 AI API Key")
    @ApiOperation("停用 AI API Key")
    public R<Boolean> disable(@AuthenticationPrincipal SohoUserDetails userDetails, @PathVariable Long id) {
        return R.success(aiUserApiKeyService.disableKey(userDetails.getId(), id));
    }

    @PutMapping("/{id}/enable")
    @Node(value = "user::ai::apiKey::enable", name = "启用 AI API Key")
    @ApiOperation("启用 AI API Key")
    public R<Boolean> enable(@AuthenticationPrincipal SohoUserDetails userDetails, @PathVariable Long id) {
        return R.success(aiUserApiKeyService.enableKey(userDetails.getId(), id));
    }

    @DeleteMapping("/{id}/destroy")
    @Node(value = "user::ai::apiKey::delete", name = "删除 AI API Key")
    @ApiOperation("删除 AI API Key")
    public R<Boolean> delete(@AuthenticationPrincipal SohoUserDetails userDetails, @PathVariable Long id) {
        return R.success(aiUserApiKeyService.deleteKey(userDetails.getId(), id));
    }

    private AiUserApiKeyView toView(AiUserApiKey item) {
        AiUserApiKeyView view = new AiUserApiKeyView();
        view.setId(item.getId());
        view.setName(item.getName());
        view.setApiKeyPrefix(item.getApiKeyPrefix());
        view.setStatus(item.getStatus());
        view.setExpireEndTime(item.getExpireEndTime());
        view.setLastUsedTime(item.getLastUsedTime());
        view.setCreatedTime(item.getCreatedTime());
        return view;
    }
}
