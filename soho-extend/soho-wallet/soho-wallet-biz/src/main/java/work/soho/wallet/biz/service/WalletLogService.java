package work.soho.wallet.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.wallet.biz.domain.WalletLog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface WalletLogService extends IService<WalletLog> {
    /**
     * 统计指定时间区间钱包变更金额
     */
    BigDecimal sumAmount(Long walletId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定时间区间钱包进账金额
     */
    BigDecimal sumIncomeAmount(Long walletId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定时间区间钱包出账金额
     */
    BigDecimal sumOutcomeAmount(Long walletId, LocalDateTime startTime, LocalDateTime endTime);
}