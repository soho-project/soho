package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductSku;
import work.soho.shop.biz.mapper.ShopProductSkuMapper;
import work.soho.shop.biz.service.ShopProductSkuService;

@RequiredArgsConstructor
@Service
public class ShopProductSkuServiceImpl extends ServiceImpl<ShopProductSkuMapper, ShopProductSku>
    implements ShopProductSkuService{

}