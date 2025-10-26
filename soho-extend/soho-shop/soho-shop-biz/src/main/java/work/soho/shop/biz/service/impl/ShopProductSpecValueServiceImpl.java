package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.OnBeforeDelete;
import work.soho.common.database.event.DeleteEvent;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.domain.ShopProductSpecValue;
import work.soho.shop.biz.mapper.ShopProductSpecValueMapper;
import work.soho.shop.biz.service.ShopProductSpecValueService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShopProductSpecValueServiceImpl extends ServiceImpl<ShopProductSpecValueMapper, ShopProductSpecValue>
    implements ShopProductSpecValueService{
    @OnBeforeDelete(entityType = ShopProductInfo.class)
    public void handleBeforeDelete(DeleteEvent event) {
        // 查找所有的sku
        List<ShopProductSpecValue> skus = list(new LambdaQueryWrapper<ShopProductSpecValue>().in(ShopProductSpecValue::getProductId,
                event.getEntityIds().stream().map(id -> (Long)id).collect(Collectors.toList())
        ));
        removeBatchByIds(skus.stream().map(ShopProductSpecValue::getId).collect(Collectors.toList()));
    }
}