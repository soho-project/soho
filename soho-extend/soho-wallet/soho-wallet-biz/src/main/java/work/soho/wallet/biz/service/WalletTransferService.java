package work.soho.wallet.biz.service;

import work.soho.wallet.biz.domain.WalletTransfer;
import com.baomidou.mybatisplus.extension.service.IService;
import java.math.BigDecimal;

public interface WalletTransferService extends IService<WalletTransfer> {
    WalletTransfer transfer(Long userId, Long walletId, Long toWalletId, BigDecimal amount, String remark);
}