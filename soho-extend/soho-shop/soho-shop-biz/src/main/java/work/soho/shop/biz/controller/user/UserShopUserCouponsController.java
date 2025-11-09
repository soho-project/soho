package work.soho.shop.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.shop.api.vo.ShopUserCouponsVo;
import work.soho.shop.biz.domain.ShopCoupons;
import work.soho.shop.biz.domain.ShopUserCoupons;
import work.soho.shop.biz.enums.ShopUserCouponsEnums;
import work.soho.shop.biz.service.ShopCouponsService;
import work.soho.shop.biz.service.ShopUserCouponsService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import work.soho.common.core.util.BeanUtils;

;
/**
 * 用户优惠券表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/user/shopUserCoupons" )
public class UserShopUserCouponsController {

    private final ShopUserCouponsService shopUserCouponsService;

    private final ShopCouponsService shopCouponsService;

    /**
     * 查询用户优惠券表列表
     */
    @GetMapping("/list")
    @Node(value = "user::shopUserCoupons::list", name = "获取 用户优惠券表 列表")
    public R<PageSerializable<ShopUserCoupons>> list(ShopUserCoupons shopUserCoupons)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopUserCoupons> lqw = new LambdaQueryWrapper<ShopUserCoupons>();
        lqw.eq(shopUserCoupons.getId() != null, ShopUserCoupons::getId ,shopUserCoupons.getId());
        lqw.eq(shopUserCoupons.getUserId() != null, ShopUserCoupons::getUserId ,shopUserCoupons.getUserId());
        lqw.eq(shopUserCoupons.getCouponId() != null, ShopUserCoupons::getCouponId ,shopUserCoupons.getCouponId());
        lqw.like(StringUtils.isNotBlank(shopUserCoupons.getCouponCode()),ShopUserCoupons::getCouponCode ,shopUserCoupons.getCouponCode());
        lqw.eq(shopUserCoupons.getStatus() != null, ShopUserCoupons::getStatus ,shopUserCoupons.getStatus());
        lqw.eq(shopUserCoupons.getUsedTime() != null, ShopUserCoupons::getUsedTime ,shopUserCoupons.getUsedTime());
        lqw.eq(shopUserCoupons.getOrderId() != null, ShopUserCoupons::getOrderId ,shopUserCoupons.getOrderId());
        lqw.eq(shopUserCoupons.getReceivedAt() != null, ShopUserCoupons::getReceivedAt ,shopUserCoupons.getReceivedAt());
        lqw.eq(shopUserCoupons.getExpiredAt() != null, ShopUserCoupons::getExpiredAt ,shopUserCoupons.getExpiredAt());
        List<ShopUserCoupons> list = shopUserCouponsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户优惠券表列表
     */
    @GetMapping("/getUserCoupons")
    public R<List<ShopUserCouponsVo>> getUserCoupons(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        List<ShopUserCoupons> list = shopUserCouponsService.getUserCoupons(sohoUserDetails.getId());
        List<Long> couponIds = list.stream().map(ShopUserCoupons::getCouponId).collect(Collectors.toList());

        Map<Long, ShopCoupons> couponsMap = shopCouponsService.listByIds(couponIds).stream().collect(Collectors.toMap(ShopCoupons::getId, v -> v));

        List<ShopUserCouponsVo> voList = list.stream().map(item -> {
            ShopCoupons coupon = couponsMap.get(item.getCouponId());
            ShopUserCouponsVo vo = BeanUtils.copy(coupon, ShopUserCouponsVo.class);
            vo.setId(item.getId());
            vo.setCode(item.getCouponCode());
            // TODO 检查优惠劵是否可用
            return vo;
        }).collect(Collectors.toList());

        return R.success(voList);
    }

    /**
     * 获取用户优惠券表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::shopUserCoupons::getInfo", name = "获取 用户优惠券表 详细信息")
    public R<ShopUserCoupons> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopUserCouponsService.getById(id));
    }

    /**
     * 新增用户优惠券表
     */
    @PostMapping
    @Node(value = "user::shopUserCoupons::add", name = "新增 用户优惠券表")
    public R<Boolean> add(@RequestBody ShopUserCoupons shopUserCoupons) {
        return R.success(shopUserCouponsService.save(shopUserCoupons));
    }

    /**
     * 修改用户优惠券表
     */
    @PutMapping
    @Node(value = "user::shopUserCoupons::edit", name = "修改 用户优惠券表")
    public R<Boolean> edit(@RequestBody ShopUserCoupons shopUserCoupons) {
        return R.success(shopUserCouponsService.updateById(shopUserCoupons));
    }

    /**
     * 删除用户优惠券表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::shopUserCoupons::remove", name = "删除 用户优惠券表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopUserCouponsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取用户未使用的优惠券数量
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/unuseCount")
    public R<Long> unuseCount(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        return R.success(shopUserCouponsService.count(
                new LambdaQueryWrapper<ShopUserCoupons>()
                        .eq(ShopUserCoupons::getUserId, sohoUserDetails.getId())
                        .eq(ShopUserCoupons::getStatus, ShopUserCouponsEnums.Status.UNUSED.getId())
        ));
    }
}