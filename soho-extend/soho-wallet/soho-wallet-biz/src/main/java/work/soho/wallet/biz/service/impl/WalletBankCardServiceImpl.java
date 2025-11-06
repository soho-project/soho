package work.soho.wallet.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.wallet.biz.domain.WalletBankCard;
import work.soho.wallet.biz.mapper.WalletBankCardMapper;
import work.soho.wallet.biz.service.WalletBankCardService;

@RequiredArgsConstructor
@Service
public class WalletBankCardServiceImpl extends ServiceImpl<WalletBankCardMapper, WalletBankCard>
    implements WalletBankCardService{

    @Override
    public WalletBankCard getWithUserId(Long userId, Long id) {
        LambdaQueryWrapper<WalletBankCard> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WalletBankCard::getId, id);
        lambdaQueryWrapper.eq(WalletBankCard::getUserId, userId);
        if (getOne(lambdaQueryWrapper) != null) {
            return getOne(lambdaQueryWrapper);
        }
        return null;
    }
}