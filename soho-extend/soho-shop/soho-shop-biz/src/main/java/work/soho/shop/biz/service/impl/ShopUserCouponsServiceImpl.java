package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopUserCoupons;
import work.soho.shop.biz.mapper.ShopUserCouponsMapper;
import work.soho.shop.biz.service.ShopUserCouponsService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ShopUserCouponsServiceImpl extends ServiceImpl<ShopUserCouponsMapper, ShopUserCoupons>
    implements ShopUserCouponsService{
    public List<ShopUserCoupons> getUserCoupons(Long userId) {
        List<ShopUserCoupons> userCouponsList =  this.list(new LambdaQueryWrapper<ShopUserCoupons>()
                .eq(ShopUserCoupons::getUserId, userId)
                .le(ShopUserCoupons::getReceivedAt, LocalDateTime.now())
        );

        return userCouponsList;
    }
}