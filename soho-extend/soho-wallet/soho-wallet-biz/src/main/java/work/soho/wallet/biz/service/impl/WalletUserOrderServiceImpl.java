package work.soho.wallet.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.BeanUtils;
import work.soho.wallet.api.dto.CreateUserOrderDTO;
import work.soho.wallet.api.service.WalletUserOrderApiService;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.domain.WalletUserOrder;
import work.soho.wallet.biz.mapper.WalletTypeMapper;
import work.soho.wallet.biz.mapper.WalletUserOrderMapper;
import work.soho.wallet.biz.service.WalletUserOrderService;

@RequiredArgsConstructor
@Service
public class WalletUserOrderServiceImpl extends ServiceImpl<WalletUserOrderMapper, WalletUserOrder>
    implements WalletUserOrderService, WalletUserOrderApiService {

    private final WalletTypeMapper walletTypeMapper;

    /**
     * 创建支付单
     *
     * @param createUserOrderDTO
     * @return
     */
    @Override
    public CreateUserOrderDTO createPayOrder(CreateUserOrderDTO createUserOrderDTO) {
        // 获取钱包类型
        WalletType type = walletTypeMapper.selectOne(new LambdaQueryWrapper<WalletType>().eq(WalletType::getName, createUserOrderDTO.getWalletTypeName()));
        Assert.notNull(type, "钱包类型不存在");
        WalletUserOrder walletUserOrder = BeanUtils.copy(createUserOrderDTO, WalletUserOrder.class);
        walletUserOrder.setWalletType(type.getId());
        this.save(walletUserOrder);
        return BeanUtils.copy(walletUserOrder, CreateUserOrderDTO.class);
    }

    @Override
    public WalletUserOrder getByNo(String no) {
        return this.getOne(new LambdaQueryWrapper<WalletUserOrder>().eq(WalletUserOrder::getNo, no));
    }
}