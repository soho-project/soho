package work.soho.wallet.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.wallet.biz.domain.WalletWithdrawalOrder;
import work.soho.wallet.biz.mapper.WalletWithdrawalOrderMapper;
import work.soho.wallet.biz.service.WalletWithdrawalOrderService;

@RequiredArgsConstructor
@Service
public class WalletWithdrawalOrderServiceImpl extends ServiceImpl<WalletWithdrawalOrderMapper, WalletWithdrawalOrder>
    implements WalletWithdrawalOrderService{

}