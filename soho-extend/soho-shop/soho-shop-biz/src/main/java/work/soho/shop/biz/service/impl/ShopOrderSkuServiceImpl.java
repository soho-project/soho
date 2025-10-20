package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopOrderSku;
import work.soho.shop.biz.mapper.ShopOrderSkuMapper;
import work.soho.shop.biz.service.ShopOrderSkuService;

@RequiredArgsConstructor
@Service
public class ShopOrderSkuServiceImpl extends ServiceImpl<ShopOrderSkuMapper, ShopOrderSku>
    implements ShopOrderSkuService{

}