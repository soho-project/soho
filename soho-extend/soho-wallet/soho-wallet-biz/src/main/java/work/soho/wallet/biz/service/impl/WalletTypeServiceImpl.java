package work.soho.wallet.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.wallet.api.dto.WalletTypeDTO;
import work.soho.wallet.api.service.WalletTypeApiService;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.mapper.WalletTypeMapper;
import work.soho.wallet.biz.service.WalletTypeService;
import work.soho.wallet.biz.enums.WalletTypeEnums;

import java.math.BigDecimal;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class WalletTypeServiceImpl extends ServiceImpl<WalletTypeMapper, WalletType>
    implements WalletTypeService, WalletTypeApiService {

    public WalletType getByName(String name) {
        return getOne(new LambdaQueryWrapper<WalletType>().eq(WalletType::getName, name).eq(WalletType::getStatus, WalletTypeEnums.Status.ACTIVE.getId()));
    }

    @Override
    public BigDecimal getCommission(Integer id, BigDecimal amount) {
        WalletType walletType = getById(id);
        return getCommission(walletType, amount);
    }

    @Override
    public BigDecimal getCommission(WalletType walletType, BigDecimal amount) {
        if(walletType.getWithdrawalMinAmount().compareTo(amount) > 0) {
            throw new RuntimeException("提现金额不能小于最小提现金额");
        }
        BigDecimal commission = BigDecimal.ZERO;
        if(walletType.getWithdrawalCommissionRate().compareTo(BigDecimal.ZERO) > 0) {
            commission = amount.multiply(walletType.getWithdrawalCommissionRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
            if(commission.compareTo(walletType.getWithdrawalMinCommission()) < 0) {
                commission = walletType.getWithdrawalMinCommission();
            }
        }

        if(commission.compareTo(amount) > 0) {
            throw new RuntimeException("手续费不能大于提现金额。");
        }

        return commission;
    }

    @Override
    public WalletTypeDTO getWalletTypeById(Integer walletTypeId) {
        WalletType walletType = this.getById(walletTypeId);
        if (Objects.nonNull(walletType) && Objects.isNull(walletType.getRate())) {
            walletType.setRate(BigDecimal.ONE);
        }
        return BeanUtil.copyProperties(walletType, WalletTypeDTO.class);
    }
}