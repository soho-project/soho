package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopCouponUsageLogs;
import work.soho.shop.biz.mapper.ShopCouponUsageLogsMapper;
import work.soho.shop.biz.service.ShopCouponUsageLogsService;

@RequiredArgsConstructor
@Service
public class ShopCouponUsageLogsServiceImpl extends ServiceImpl<ShopCouponUsageLogsMapper, ShopCouponUsageLogs>
    implements ShopCouponUsageLogsService{

}