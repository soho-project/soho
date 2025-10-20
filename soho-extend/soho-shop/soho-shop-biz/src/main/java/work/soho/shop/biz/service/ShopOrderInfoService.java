package work.soho.shop.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.shop.api.request.OrderCreateRequest;
import work.soho.shop.biz.domain.ShopOrderInfo;

public interface ShopOrderInfoService extends IService<ShopOrderInfo> {
    public ShopOrderInfo createOrder(OrderCreateRequest request);
}