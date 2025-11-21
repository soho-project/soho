package work.soho.shop.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.shop.api.request.OrderCreateRequest;
import work.soho.shop.api.vo.ProductSkuVo;
import work.soho.shop.api.vo.ShopUserCouponsVo;
import work.soho.shop.biz.domain.ShopCouponApplyRanges;
import work.soho.shop.biz.domain.ShopCoupons;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.domain.ShopUserCoupons;
import work.soho.shop.biz.enums.ShopCouponUsageLogsEnums;
import work.soho.shop.biz.enums.ShopUserCouponsEnums;
import work.soho.shop.biz.service.*;
import work.soho.shop.biz.domain.ShopInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final ShopProductInfoService shopProductInfoService;
    private final ShopCouponApplyRangesService shopCouponApplyRangesService;
    private final ShopInfoService shopInfoService;

    /**
     * 查询用户优惠券表列表
     */
    @GetMapping("/list")
    @Node(value = "user::shopUserCoupons::list", name = "获取 用户优惠券表 列表")
    public R<PageSerializable<ShopUserCouponsVo>> list(ShopUserCoupons shopUserCoupons, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopUserCoupons> lqw = new LambdaQueryWrapper<ShopUserCoupons>();
        lqw.eq(ShopUserCoupons::getUserId ,sohoUserDetails.getId());
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

        List<ShopUserCouponsVo> listVo = new ArrayList<>();
        if(list != null || !list.isEmpty()) {
            // 获取所有优惠劵ID
            List<Long> couponIds = list.stream().map(ShopUserCoupons::getCouponId).collect(Collectors.toList());
            Map<Long, ShopCoupons> couponsMap = shopCouponsService.listByIds(couponIds).stream().collect(Collectors.toMap(ShopCoupons::getId, v -> v));

            // 获取店铺信息
            List<Integer> shopIds = couponsMap.values().stream().map(ShopCoupons::getShopId).collect(Collectors.toList());
            Map<Integer, ShopInfo> shopInfoMap = shopInfoService.listByIds(shopIds).stream().collect(Collectors.toMap(ShopInfo::getId, v -> v));

            listVo = list.stream().map(item -> {
                ShopUserCouponsVo vo = BeanUtils.copy(couponsMap.get(item.getCouponId()), ShopUserCouponsVo.class);
                if(couponsMap.get(item.getCouponId()).getShopId() != null && couponsMap.get(item.getCouponId()).getShopId()>0) {
                    vo.setShopName(shopInfoMap.get(couponsMap.get(item.getCouponId()).getShopId()).getName());
                } else {
                    vo.setShopName("全平台");
                }

                vo.setId(item.getId());
                vo.setCode(item.getCouponCode());
                return vo;
            }).collect(Collectors.toList());
        }

        Page<ShopUserCouponsVo> voPage = new Page<>();
        voPage.setTotal(((Page)list).getTotal());
        voPage.addAll(listVo);

        return R.success(new PageSerializable<>(voPage));
    }

    /**
     * 获取用户优惠券表列表
     */
    @PostMapping("/getUserCoupons")
    public R<List<ShopUserCouponsVo>> getUserCoupons(@AuthenticationPrincipal SohoUserDetails sohoUserDetails,@RequestBody OrderCreateRequest request) {
        List<ShopUserCoupons> list = shopUserCouponsService.getUserCoupons(sohoUserDetails.getId());
        List<Long> couponIds = list.stream().map(ShopUserCoupons::getCouponId).collect(Collectors.toList());

        if(couponIds == null || couponIds.isEmpty()) {
            return R.success(new ArrayList<>());
        }

        Map<Long, ShopCoupons> couponsMap = shopCouponsService.listByIds(couponIds).stream().collect(Collectors.toMap(ShopCoupons::getId, v -> v));

        // 获取当前订单所有的商品信息
        List<Long> productIds = request.getProducts().stream().map(ProductSkuVo::getProductId).collect(Collectors.toList());
        List<ShopProductInfo> products = shopProductInfoService.listByIds(productIds);
        Map<Long, ShopProductInfo> productsMap = products.stream().collect(Collectors.toMap(ShopProductInfo::getId, v -> v));
        // 获取所有商品的分类id
        List<Long> categoryIds = products.stream().map(ShopProductInfo::getCategoryId).collect(Collectors.toList());
        // 获取商品所有的店铺ID
        List<Integer> shopIds = products.stream().map(ShopProductInfo::getShopId).collect(Collectors.toList());

        List<ShopUserCouponsVo> voList = list.stream().map(item -> {
            ShopCoupons coupon = couponsMap.get(item.getCouponId());
            // 获取符合店铺的产品ID
            List<Long> shopProductIds;
            if(coupon.getShopId() != null) {
                shopProductIds = products.stream().filter(product -> product.getShopId().equals(coupon.getShopId())).map(ShopProductInfo::getId).collect(Collectors.toList());
            } else {
                shopProductIds = productIds;
            }
            ShopUserCouponsVo vo = BeanUtils.copy(coupon, ShopUserCouponsVo.class);
            vo.setId(item.getId());
            vo.setCode(item.getCouponCode());

            // 获取优惠劵对应的限制条件
            List<ShopCouponApplyRanges> ranges = shopCouponApplyRangesService.list(new LambdaQueryWrapper<ShopCouponApplyRanges>()
                .eq(ShopCouponApplyRanges::getCouponId, coupon.getId()));

            // 使用代码
            boolean isActive = ranges.stream().anyMatch(range -> {
                ShopCouponUsageLogsEnums.ScopeType scopeType = ShopCouponUsageLogsEnums.ScopeType.fromId(range.getScopeType());
                boolean active = false;
                switch(scopeType) {
                    case PRODUCT:
                        active = shopProductIds.contains(range.getScopeId());
                        break;
                    case CATEGORY:
                        active = categoryIds.contains(range.getScopeId()) && shopProductIds.contains(range.getScopeId());
                        break;
                        default:
                        active = false;
                }

                return active;
            });

            vo.setIsActive(isActive);
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