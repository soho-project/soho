package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.database.annotation.OnBeforeDelete;
import work.soho.common.database.event.DeleteEvent;
import work.soho.shop.biz.domain.ShopCouponApplyRanges;
import work.soho.shop.biz.mapper.ShopCouponApplyRangesMapper;
import work.soho.shop.biz.service.ShopCouponApplyRangesService;

@RequiredArgsConstructor
@Service
public class ShopCouponApplyRangesServiceImpl extends ServiceImpl<ShopCouponApplyRangesMapper, ShopCouponApplyRanges>
    implements ShopCouponApplyRangesService{
    @OnBeforeDelete(entityType = ShopCouponApplyRanges.class)
    public void onBeforeRemove(DeleteEvent event) {
        remove(new LambdaQueryWrapper<ShopCouponApplyRanges>().in(ShopCouponApplyRanges::getCouponId, event.getEntityIds()));
    }
}