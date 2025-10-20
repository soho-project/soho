package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopCoupons;
import work.soho.shop.biz.mapper.ShopCouponsMapper;
import work.soho.shop.biz.service.ShopCouponsService;

@RequiredArgsConstructor
@Service
public class ShopCouponsServiceImpl extends ServiceImpl<ShopCouponsMapper, ShopCoupons>
    implements ShopCouponsService{

}