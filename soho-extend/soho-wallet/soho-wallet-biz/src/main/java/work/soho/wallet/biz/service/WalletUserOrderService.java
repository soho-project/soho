package work.soho.wallet.biz.service;

import work.soho.wallet.biz.domain.WalletUserOrder;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WalletUserOrderService extends IService<WalletUserOrder> {
    WalletUserOrder getByNo(String no);
}