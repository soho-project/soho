package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.OnBeforeDelete;
import work.soho.common.database.event.DeleteEvent;
import work.soho.shop.biz.domain.ShopOrderInfo;
import work.soho.shop.biz.domain.ShopOrderSku;
import work.soho.shop.biz.mapper.ShopOrderSkuMapper;
import work.soho.shop.biz.service.ShopOrderSkuService;

@RequiredArgsConstructor
@Service
public class ShopOrderSkuServiceImpl extends ServiceImpl<ShopOrderSkuMapper, ShopOrderSku>
    implements ShopOrderSkuService{

    @OnBeforeDelete(entityType = ShopOrderInfo.class)
    public void onBeforeRemove(DeleteEvent event) {
        remove(new LambdaQueryWrapper<ShopOrderSku>().in(ShopOrderSku::getOrderId, event.getEntityIds()));
    }
}