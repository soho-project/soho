package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopUserCoupons;
import work.soho.shop.biz.mapper.ShopUserCouponsMapper;
import work.soho.shop.biz.service.ShopUserCouponsService;

@RequiredArgsConstructor
@Service
public class ShopUserCouponsServiceImpl extends ServiceImpl<ShopUserCouponsMapper, ShopUserCoupons>
    implements ShopUserCouponsService{

}