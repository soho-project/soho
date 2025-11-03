package work.soho.shop.biz.service;

import work.soho.shop.api.vo.UserCartVo;
import work.soho.shop.biz.domain.ShopCartItems;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ShopCartItemsService extends IService<ShopCartItems> {
    public UserCartVo getUserCart(Long userId);
}