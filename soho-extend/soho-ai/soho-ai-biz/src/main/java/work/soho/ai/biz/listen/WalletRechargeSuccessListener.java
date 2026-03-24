package work.soho.ai.biz.listen;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.event.WalletRechargeSuccessEvent;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletRecharge;
import work.soho.wallet.biz.enums.WalletRechargeEnums;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletRechargeService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class WalletRechargeSuccessListener {
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.20");

    private final UserInfoService userInfoService;
    private final WalletRechargeService walletRechargeService;
    private final WalletInfoService walletInfoService;
    private final WalletInfoApiService walletInfoApiService;

    @EventListener(WalletRechargeSuccessEvent.class)
    public void onWalletRechargeSuccess(WalletRechargeSuccessEvent event) {
        if (event.getUserId() == null || event.getWalletId() == null || event.getAmount() == null
                || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        UserInfo userInfo = userInfoService.getById(event.getUserId());
        if (userInfo == null || userInfo.getReferrerId() == null || userInfo.getReferrerId() <= 0
                || userInfo.getReferrerId().equals(userInfo.getId())) {
            return;
        }

        Long rechargedCount = walletRechargeService.count(new LambdaQueryWrapper<WalletRecharge>()
                .eq(WalletRecharge::getUserId, event.getUserId())
                .eq(WalletRecharge::getStatus, WalletRechargeEnums.Status.RECHARGED.getId()));
        if (rechargedCount == null || rechargedCount != 1L) {
            return;
        }

        WalletInfo walletInfo = walletInfoService.getById(event.getWalletId());
        if (walletInfo == null || walletInfo.getType() == null) {
            return;
        }

        BigDecimal commissionAmount = event.getAmount()
                .multiply(COMMISSION_RATE)
                .setScale(4, RoundingMode.HALF_UP);
        if (commissionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        walletInfoApiService.changeWalletAmount(
                userInfo.getReferrerId(),
                walletInfo.getType(),
                WalletLogEnums.BizId.PERFORMANCE_SHARING.getId(),
                event.getCode(),
                commissionAmount,
                "邀请用户首次充值奖励20%"
        );
    }
}
