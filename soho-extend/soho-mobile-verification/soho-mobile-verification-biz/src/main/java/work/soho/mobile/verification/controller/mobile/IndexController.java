package work.soho.mobile.verification.controller.mobile;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.mobile.verification.annotations.RequireValidToken;
import work.soho.mobile.verification.api.service.VerificationServiceApi;
import work.soho.mobile.verification.config.SohoMobileVerificationConfig;
import work.soho.mobile.verification.request.SaveMsgRequest;
import work.soho.mobile.verification.service.VerificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/client/api/mobile")
public class IndexController {
    private final VerificationService verificationService;
    private final VerificationServiceApi verificationServiceApi;
    private final SohoMobileVerificationConfig sohoConfig;

    @GetMapping("getMsgByPhoneNumber")
    public R<String> getMsgByPhoneNumber() {
        return R.success("本次验证码为  1  2  3  4  5  6;本次验证码为  1  2  3  4  5  6;本次验证码为  1  2  3  4  5  6; 播报完毕，请挂断电话。");
    }

    /**
     * 推送短信信息
     *
     * @param saveMsgRequest
     * @return
     */
    @PostMapping("pushMsg")
    @RequireValidToken(token = "mobile-verification-android-access-ey")
    public R<Boolean> pushMsg(@RequestBody SaveMsgRequest saveMsgRequest) {
        try {
            verificationService.pushMsg(saveMsgRequest.getPhoneNumber(), saveMsgRequest.getMsg());
            return R.success(true);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }


}
