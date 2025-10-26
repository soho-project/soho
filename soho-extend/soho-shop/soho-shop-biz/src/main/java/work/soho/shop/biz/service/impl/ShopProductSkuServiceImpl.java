package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.*;
import work.soho.common.database.event.DeleteEvent;
import work.soho.common.database.event.SaveEvent;
import work.soho.common.database.event.UpdateEvent;
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
    implements ShopProductSkuService {
    @OnBeforeDelete(entityType = ShopProductInfo.class)
    public void handleBeforeDelete(DeleteEvent event) {
        log.debug("在sku服务监听到了产品删除事件: OnBeforeDelete");
        // 查找所有的sku
        List<ShopProductSku> skus = list(new LambdaQueryWrapper<ShopProductSku>().in(ShopProductSku::getProductId,
                event.getEntityIds().stream().map(id -> (Long)id).collect(Collectors.toList())
        ));
        removeBatchByIds(skus.stream().map(ShopProductSku::getId).collect(Collectors.toList()));
    }

    @OnAfterDelete(entityType = ShopProductInfo.class)
    public void handleAfterDelete(DeleteEvent event) {
        log.debug("在sku服务监听到了产品删除事件: OnAfterDelete");
        System.out.println(event);
    }

    @OnBeforeSave(entityType = ShopProductInfo.class)
    public void onBeforeSaveProduct(SaveEvent event) {
        log.debug("在sku服务监听到了产品保存事件: OnBeforeSave");
        System.out.println(event);
    }

    @OnAfterSave(entityType = ShopProductInfo.class)
    public void onAfterSaveProduct(SaveEvent event) {
        log.debug("在sku服务监听到了产品保存事件: OnAfterSave");
        System.out.println(event);
    }

    @OnAfterUpdate(entityType = ShopProductInfo.class)
    public void onAfterUpdateProduct(UpdateEvent event) {
        log.debug("在sku服务监听到了产品更新事件: OnAfterUpdate");
        System.out.println(event);
    }

    @OnBeforeUpdate(entityType = ShopProductInfo.class)
    public void onBeforeUpdateProduct(UpdateEvent event) {
        log.debug("在sku服务监听到了产品更新事件：OnBeforeUpdate");
        System.out.println(event);
    }
}