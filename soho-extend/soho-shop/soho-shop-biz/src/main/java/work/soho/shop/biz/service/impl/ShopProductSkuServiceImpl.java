package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.OnBeforeDelete;
import work.soho.common.database.annotation.PublishDeleteNotify;
import work.soho.common.database.event.BeforeBatchDeleteEvent;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.domain.ShopProductSku;
import work.soho.shop.biz.mapper.ShopProductSkuMapper;
import work.soho.shop.biz.service.ShopProductSkuService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@PublishDeleteNotify()
public class ShopProductSkuServiceImpl extends ServiceImpl<ShopProductSkuMapper, ShopProductSku>
    implements ShopProductSkuService{
    @OnBeforeDelete(entityType = ShopProductInfo.class)
    public void handleBeforeDelete(BeforeBatchDeleteEvent event) {
        // 查找所有的sku
        List<ShopProductSku> skus = list(new LambdaQueryWrapper<ShopProductSku>().in(ShopProductSku::getProductId,
                event.getEntityIds().stream().map(id -> (Long)id).collect(Collectors.toList())
        ));
        removeBatchByIds(skus.stream().map(ShopProductSku::getId).collect(Collectors.toList()));
    }
}