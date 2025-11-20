package work.soho.wallet.biz.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.service.UserSmsApiService;
import work.soho.wallet.api.request.UpdatePayPasswordRequest;
import work.soho.wallet.biz.domain.WalletUser;
import work.soho.wallet.biz.service.WalletUserService;

;
/**
 * 钱包用户Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletUser" )
public class UserWalletUserController {

    private final WalletUserService walletUserService;
    private final UserSmsApiService userSmsApiService;

    /**
     * 修改支付密码
     */
    @PutMapping("/changePayPassword")
    public R<Boolean> changePayPassword(@RequestBody UpdatePayPasswordRequest updatePayPasswordRequest, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        // 校验两次密码是否一致
        if (!updatePayPasswordRequest.getNewPayPassword().equals(updatePayPasswordRequest.getConfirmPayPassword())) {
            return R.error("两次密码不一致");
        }

        // 校验验证码是否正确
        if (!userSmsApiService.verifySmsCaptcha(sohoUserDetails.getId(), updatePayPasswordRequest.getCaptcha())) {
            return R.error("验证码错误");
        }

        WalletUser user = walletUserService.getById(sohoUserDetails.getId());
        if(user == null) {
            user = new WalletUser();
            user.setUserId(sohoUserDetails.getId());
            walletUserService.save(user);
        }
        // 密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPayPassword(encoder.encode(updatePayPasswordRequest.getNewPayPassword()));
        return R.success(walletUserService.updateById(user));
    }
}