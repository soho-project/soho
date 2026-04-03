package work.soho.wallet.biz.listen;

import cn.hutool.core.lang.Assert;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.soho.common.core.util.BeanUtils;
import work.soho.pay.api.event.PayCallbackEvent;
import work.soho.wallet.api.enums.WalletTypeNameEnums;
import work.soho.wallet.api.event.WalletRechargeSuccessEvent;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletRecharge;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.enums.WalletRechargeEnums;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletRechargeService;
import work.soho.wallet.biz.service.WalletTypeService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RechargePayedListen {
    private final WalletRechargeService walletRechargeService;
    private final WalletTypeService walletTypeService;
    private final WalletInfoService walletInfoService;
    private final ApplicationContext applicationContext;

    /**
     * 处理支付成功事件。
     *
     * <p>在存在事务时延迟到事务提交后再执行，避免主库事务上下文影响钱包库动态数据源切换。</p>
     * <p>在不存在事务时（如常规支付回调）允许立即执行。</p>
     *
     * @param event 支付回调事件
     */
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @DSTransactional
    @EventListener
    public void onApplicationEvent(PayCallbackEvent event) {
        System.out.println("充值成功");
        // 检查是否是充值单
        WalletRecharge walletRecharge = walletRechargeService.getOne(new LambdaQueryWrapper<WalletRecharge>().eq(WalletRecharge::getCode, event.getOutTradeNo()));
        if(walletRecharge == null) {
            return;
        }

        if(walletRecharge.getStatus() != null && walletRecharge.getStatus().equals(WalletRechargeEnums.Status.RECHARGED.getId())) {
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

        // 发送钱包充值成功事件
        WalletRechargeSuccessEvent walletRechargeSuccessEvent = BeanUtils.copy(walletRecharge, WalletRechargeSuccessEvent.class);
        walletRechargeSuccessEvent.setTransactionNo(event.getTransactionNo());
        applicationContext.publishEvent(walletRechargeSuccessEvent);
    }
}
