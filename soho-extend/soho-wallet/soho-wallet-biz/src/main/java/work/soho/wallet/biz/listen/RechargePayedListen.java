package work.soho.wallet.biz.listen;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import work.soho.pay.api.event.PayCallbackEvent;
import work.soho.wallet.api.enums.WalletTypeNameEnums;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.enums.WalletRechargeEnums;
import work.soho.wallet.biz.service.WalletRechargeService;
import work.soho.wallet.biz.domain.WalletRecharge;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletTypeService;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RechargePayedListen {
    private final WalletRechargeService walletRechargeService;
    private final WalletTypeService walletTypeService;
    private final WalletInfoService walletInfoService;

    @EventListener
    @Transactional
    public void onApplicationEvent(PayCallbackEvent event) {
        System.out.println("充值成功");
        // 检查是否是充值单
        WalletRecharge walletRecharge = walletRechargeService.getOne(new LambdaQueryWrapper<WalletRecharge>().eq(WalletRecharge::getCode, event.getOutTradeNo()));
        if(walletRecharge == null) {
            return;
        }

        // 更新充值单状态
        walletRecharge.setStatus(WalletRechargeEnums.Status.RECHARGED.getId());
        walletRecharge.setUpdatedTime(LocalDateTime.now());
        walletRechargeService.updateById(walletRecharge);

        // 更新钱包余额
        //目前只支持rmb钱包充值， 检查钱包类型是否为 rmb 类型
        LambdaQueryWrapper<WalletType> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletType::getName, WalletTypeNameEnums.RMB.getName());
        WalletType walletType = walletTypeService.getOne(lqw);
        Assert.notNull(walletType, "钱包类型不存在");

        WalletInfo info = walletInfoService.getByUserIdAndType(walletRecharge.getUserId(), walletType.getId());
        Assert.notNull(info, "钱包不存在");

        walletInfoService.updateAmount(info, walletRecharge.getAmount(), "钱包充值");
    }
}
