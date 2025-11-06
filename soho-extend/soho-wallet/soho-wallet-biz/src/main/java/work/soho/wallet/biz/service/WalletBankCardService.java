package work.soho.wallet.biz.service;

import work.soho.wallet.biz.domain.WalletBankCard;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WalletBankCardService extends IService<WalletBankCard> {
    WalletBankCard getWithUserId(Long userId, Long id);
}