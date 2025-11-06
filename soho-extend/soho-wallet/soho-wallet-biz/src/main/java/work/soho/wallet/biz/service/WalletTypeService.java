package work.soho.wallet.biz.service;

import work.soho.wallet.biz.domain.WalletType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface WalletTypeService extends IService<WalletType> {
    BigDecimal getCommission(Integer id, BigDecimal amount);
    BigDecimal getCommission(WalletType walletType, BigDecimal amount);
    WalletType getByName(String name);
}