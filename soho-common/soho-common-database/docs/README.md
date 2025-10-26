# 数据库组件

## 模块分库

最佳实践：

        @Around("execution(* work.soho.[模块包名].biz.service..*+.*(..)) && execution(* com.baomidou.mybatisplus.extension.service.IService+.*(..))")

## TODO 事务


## 关联删除

    // 删除发送通知； 可以直接使用注解
    @PublishDeleteNotify

    // 保存通知
    @PublishSaveNotify

    // 更新通知
    @PublishUpdateNotify

    // 在方法上添加注解可以接收删除通知
    @OnBeforeBatchDelete

## DEMO

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
        log.debug("在sku服务监听到了产品更新后事件: OnAfterUpdate");
        System.out.println(event);
    }

    @OnBeforeUpdate(entityType = ShopProductInfo.class)
    public void onBeforeUpdateProduct(UpdateEvent event) {
        log.debug("在sku服务监听到了产品更新前事件：OnBeforeUpdate");
        System.out.println(event);
    }