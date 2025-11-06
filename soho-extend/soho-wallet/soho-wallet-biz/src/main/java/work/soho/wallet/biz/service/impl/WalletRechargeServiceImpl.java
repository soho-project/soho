package work.soho.wallet.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.wallet.biz.domain.WalletRecharge;
import work.soho.wallet.biz.mapper.WalletRechargeMapper;
import work.soho.wallet.biz.service.WalletRechargeService;

@RequiredArgsConstructor
@Service
public class WalletRechargeServiceImpl extends ServiceImpl<WalletRechargeMapper, WalletRecharge>
    implements WalletRechargeService{

}