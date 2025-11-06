package work.soho.wallet.biz.service;

import work.soho.wallet.biz.domain.WalletInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.wallet.biz.domain.WalletLog;

import java.math.BigDecimal;

public interface WalletInfoService extends IService<WalletInfo> {
    WalletInfo getByUserIdAndType(Long userId, Integer type);
    WalletLog updateAmount(Long userId, Integer type, BigDecimal amount, String notes);
    WalletLog updateAmount(WalletInfo walletInfo, BigDecimal amount, String notes);
}