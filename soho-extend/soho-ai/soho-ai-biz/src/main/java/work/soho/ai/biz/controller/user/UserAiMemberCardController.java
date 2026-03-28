package work.soho.ai.biz.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.ai.biz.dto.AiUserMemberCardView;
import work.soho.ai.biz.service.AiMemberCardRedeemCodeService;
import work.soho.ai.biz.service.AiUserMemberCardService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.List;

@Api(tags = "用户 AI 会员卡")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/user/memberCard")
public class UserAiMemberCardController {
    private final AiUserMemberCardService aiUserMemberCardService;
    private final AiMemberCardRedeemCodeService aiMemberCardRedeemCodeService;

    @GetMapping("/list")
    @Node(value = "user::ai::memberCard::list", name = "获取用户会员卡列表")
    @ApiOperation("获取用户会员卡列表")
    public R<List<AiUserMemberCardView>> list(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(aiUserMemberCardService.listUserCards(userDetails.getId()));
    }

    @GetMapping("/current")
    @Node(value = "user::ai::memberCard::current", name = "获取当前生效会员卡")
    @ApiOperation("获取当前生效会员卡")
    public R<AiUserMemberCardView> current(@AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(aiUserMemberCardService.currentUserCard(userDetails.getId()).orElse(null));
    }

    @PutMapping("/select/{userCardId}")
    @Node(value = "user::ai::memberCard::select", name = "选择会员卡")
    @ApiOperation("选择会员卡")
    public R<Boolean> select(@AuthenticationPrincipal SohoUserDetails userDetails,
                             @PathVariable Long userCardId) {
        return R.success(aiUserMemberCardService.selectUserCard(userDetails.getId(), userCardId));
    }

    @PostMapping("/redeem")
    @Node(value = "user::ai::memberCard::redeem", name = "兑换并激活会员卡")
    @ApiOperation("兑换并激活会员卡")
    public R<Boolean> redeem(@AuthenticationPrincipal SohoUserDetails userDetails,
                             @RequestBody RedeemRequest request) {
        if (request == null) {
            return R.error("请求参数不能为空");
        }
        AiMemberCardRedeemCodeService.RedeemResult result = aiMemberCardRedeemCodeService.redeem(
                userDetails.getId(), request.getRedeemCode());
        if (!result.isSuccess()) {
            return R.error(result.getMessage());
        }
        return R.success(true);
    }

    @Data
    public static class RedeemRequest {
        private String redeemCode;
    }
}
