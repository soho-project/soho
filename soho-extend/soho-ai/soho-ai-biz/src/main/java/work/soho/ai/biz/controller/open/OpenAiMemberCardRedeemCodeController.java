package work.soho.ai.biz.controller.open;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.ai.biz.service.AiMemberCardRedeemCodeService;
import work.soho.common.core.result.R;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.api.annotation.OpenApi;

@Api(tags = "AI 开放接口-会员卡兑换码")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/open/memberCardRedeemCode")
public class OpenAiMemberCardRedeemCodeController {
    private final AiMemberCardRedeemCodeService aiMemberCardRedeemCodeService;

    @PostMapping("/purchaseByName")
    @ApiOperation("按会员卡名称购买兑换码并自动钱包扣款")
    @OpenApi(value = "open::ai::memberCardRedeemCode::purchaseByName", name = "按名称购买 AI 兑换码")
    public R<AiMemberCardRedeemCodeService.PurchaseRedeemCodeResult> purchaseByName(
            @AuthenticationPrincipal SohoUserDetails userDetails,
            @RequestBody PurchaseByNameRequest request) {
        if (userDetails == null || userDetails.getId() == null) {
            return R.error("用户未登录");
        }
        if (request == null) {
            return R.error("请求参数不能为空");
        }
        AiMemberCardRedeemCodeService.PurchaseRedeemCodeResult result =
                aiMemberCardRedeemCodeService.purchaseByMemberCardName(
                        userDetails.getId(),
                        request.getMemberCardName(),
                        request.getEmail()
                );
        if (!result.isSuccess()) {
            return R.error(result.getMessage());
        }
        return R.success(result);
    }

    @Data
    public static class PurchaseByNameRequest {
        private String memberCardName;
        private String email;
    }
}
