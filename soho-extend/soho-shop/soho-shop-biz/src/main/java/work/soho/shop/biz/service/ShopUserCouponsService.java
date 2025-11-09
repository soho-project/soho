package work.soho.shop.biz.service;

import work.soho.shop.biz.domain.ShopUserCoupons;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface ShopUserCouponsService extends IService<ShopUserCoupons> {
    List<ShopUserCoupons> getUserCoupons(Long userId);
}