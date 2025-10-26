package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.PublishDeleteNotify;
import work.soho.shop.biz.domain.ShopCoupons;
import work.soho.shop.biz.mapper.ShopCouponsMapper;
import work.soho.shop.biz.service.ShopCouponsService;

@PublishDeleteNotify
@RequiredArgsConstructor
@Service
public class ShopCouponsServiceImpl extends ServiceImpl<ShopCouponsMapper, ShopCoupons>
    implements ShopCouponsService{
}