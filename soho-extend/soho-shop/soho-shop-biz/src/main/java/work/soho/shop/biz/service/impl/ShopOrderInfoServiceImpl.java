package work.soho.shop.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.shop.api.request.OrderCreateRequest;
import work.soho.shop.api.vo.ProductSkuVo;
import work.soho.shop.biz.domain.*;
import work.soho.shop.biz.enums.ShopCouponApplyRangesEnums;
import work.soho.shop.biz.enums.ShopCouponsEnums;
import work.soho.shop.biz.enums.ShopOrderInfoEnums;
import work.soho.shop.biz.enums.ShopUserCouponsEnums;
import work.soho.shop.biz.mapper.*;
import work.soho.shop.biz.service.ShopInfoService;
import work.soho.shop.biz.service.ShopOrderInfoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShopOrderInfoServiceImpl extends ServiceImpl<ShopOrderInfoMapper, ShopOrderInfo>
    implements ShopOrderInfoService{

    private final ShopProductInfoMapper shopProductInfoMapper;
    private final ShopProductSkuMapper shopProductSkuMapper;
    private final ShopOrderSkuMapper shopOrderSkuMapper;
    private final ShopUserAddressesMapper shopUserAddressesMapper;
    private final ShopCouponsMapper shopCouponsMapper;
    private final ShopUserCouponsMapper shopUserCouponsMapper;
    private final ShopCouponApplyRangesMapper shopCouponApplyRangesMapper;
    private final ShopInfoService shopInfoService;

    @Override
    public ShopOrderInfo createOrder(OrderCreateRequest request) {
        // 获取用户收货地址
        ShopUserAddresses shopUserAddresses = shopUserAddressesMapper.selectById(request.getUserAddressId());
        Assert.notNull(shopUserAddresses, "收货地址不存在");
        // 检查地址是否为当前用户的地址
        Assert.isTrue(shopUserAddresses.getUserId().equals(request.getUserId()), "收货地址不属于当前用户");

        //TODO 优惠劵等信息装备

        ShopOrderInfo shopOrderInfo = new ShopOrderInfo();
        shopOrderInfo.setNo(IDGeneratorUtils.snowflake().toString());
        shopOrderInfo.setUserId(request.getUserId());
        shopOrderInfo.setDeliveryFee(request.getDeliveryFee());
        shopOrderInfo.setDiscountAmount(BigDecimal.ZERO);
//        shopOrderInfo.setAmount(BigDecimal.valueOf(100.00));
        shopOrderInfo.setStatus(ShopOrderInfoEnums.Status.PENDING.getId());
        shopOrderInfo.setPayStatus(ShopOrderInfoEnums.PayStatus.PENDING_PAYMENT.getId());
        shopOrderInfo.setFreightStatus(0);
        shopOrderInfo.setOrderType(request.getType()!=null ? request.getType() : ShopOrderInfoEnums.OrderType.PHYSICAL_ORDER.getId());
        shopOrderInfo.setSource(request.getSource()!=null ? request.getSource() : ShopOrderInfoEnums.Source.WAP.getId());
        shopOrderInfo.setRemark(request.getRemark());
        shopOrderInfo.setReceivingAddress(shopUserAddresses.getProvince()
            + " " + shopUserAddresses.getCity()
            + " " + shopUserAddresses.getDistrict()
            + " " + shopUserAddresses.getDetailAddress()
        );
        shopOrderInfo.setConsignee(shopUserAddresses.getRecipientName());
        shopOrderInfo.setReceivingPhoneNumber(shopUserAddresses.getRecipientPhone());
        Integer orderId = 1;

        BigDecimal orderProductTotalAmount = BigDecimal.ZERO;
        List<ShopOrderSku> shopOrderSkuList = new ArrayList<>();
        for(ProductSkuVo product : request.getProducts()) {
            ShopProductInfo productInfo = shopProductInfoMapper.selectById(product.getProductId());
            Assert.notNull(productInfo, "商品不存在");
            Assert.isTrue(productInfo.getShelfStatus() == 1, "商品已下架");
            Assert.isTrue(productInfo.getQty() >= product.getQty(), "商品库存不足");
            // 检查sku信息能对的上
            ShopProductSku sku = shopProductSkuMapper.selectById(product.getSkuId());
            Assert.notNull(sku, "商品sku不存在");
            Assert.isTrue(sku.getProductId().equals(productInfo.getId()), "商品sku信息不匹配");

            // 插入订单商品信息
            ShopOrderSku shopOrderSku = new ShopOrderSku()
                    .setOrderId(orderId)
                    .setProductId(productInfo.getId())
                    .setSkuId(product.getSkuId())
                    .setName(product.getName())
                    .setMainImage(
                            StringUtils.isNotBlank(sku.getMainImage()) ?
                                    sku.getMainImage() :
                                    productInfo.getMainImage()
                    )
                    .setAmount(sku.getSellingPrice())
                    .setQty(product.getQty())
                    .setTotalAmount(sku.getSellingPrice().multiply(BigDecimal.valueOf(product.getQty())));

            // 计算订单总金额
            orderProductTotalAmount = orderProductTotalAmount.add(shopOrderSku.getTotalAmount());

            shopOrderSkuList.add(shopOrderSku);

        }

        //TODO 检查商品所属店铺， 店铺状态是否正常; 根据店铺进行拆单
//        ShopInfo shopInfo = shopInfoService.getById(request.getShopId());

        // 优惠劵计算
        ShopCouponUsageLogs shopCouponUsageLogs = createCouponUsageLogs(request);
        if(shopCouponUsageLogs != null) {
            shopOrderInfo.setDiscountAmount(shopCouponUsageLogs.getDiscountAmount());
        }

        // 保存订单信息
        shopOrderInfo.setProductTotalAmount(orderProductTotalAmount);
        shopOrderInfo.setAmount(orderProductTotalAmount.add(shopOrderInfo.getDeliveryFee()).subtract(shopOrderInfo.getDiscountAmount()));
        save(shopOrderInfo);
        // 保存订单商品信息
        for(ShopOrderSku shopOrderSku : shopOrderSkuList) {
            shopOrderSku.setOrderId(shopOrderInfo.getId());
            shopOrderSkuMapper.insert(shopOrderSku);
            // 扣减库存
            ShopProductInfo productInfo = shopProductInfoMapper.selectById(shopOrderSku.getProductId());
            shopProductInfoMapper.updateById(
                    new ShopProductInfo()
                            .setId(shopOrderSku.getProductId())
                            .setQty(productInfo.getQty() - shopOrderSku.getQty()));

            // 检查是否sku商品
            if (shopOrderSku.getSkuId() != null) {
                ShopProductSku skuInfo = shopProductSkuMapper.selectById(shopOrderSku.getSkuId());
                Assert.notNull(skuInfo, "商品sku不存在");
                Assert.isTrue(skuInfo.getQty() >= shopOrderSku.getQty(), "商品sku库存不足");
                shopProductSkuMapper.updateById(
                        new ShopProductSku()
                                .setId(shopOrderSku.getSkuId())
                                .setQty(skuInfo.getQty() - shopOrderSku.getQty())
                );
            }
        }

        return shopOrderInfo;
    }

    /**
     * 创建优惠劵使用记录
     *
     * @param request
     * @return
     */
    private ShopCouponUsageLogs createCouponUsageLogs(OrderCreateRequest request) {
        // 查找优惠劵相关信息
        ShopCoupons shopCoupons = shopCouponsMapper.selectById(request.getCouponId());
        Assert.notNull(shopCoupons, "优惠劵不存在");
        Assert.equals(shopCoupons.getStatus(), ShopCouponsEnums.Status.ENABLE.getId(), "优惠劵已禁用");
        // 检查当前下单用户是否有优惠劵; expired_at > now()
        ShopUserCoupons shopUserCoupons = shopUserCouponsMapper.selectOne(
                new LambdaQueryWrapper<ShopUserCoupons>()
                        .eq(ShopUserCoupons::getUserId, request.getUserId())
                        .eq(ShopUserCoupons::getCouponId, request.getCouponId())
                        .eq(ShopUserCoupons::getStatus, ShopUserCouponsEnums.Status.UNUSED.getId())
                .and(wrapper -> wrapper.gt(ShopUserCoupons::getExpiredAt, LocalDateTime.now()))
        );
        Assert.notNull(shopUserCoupons, "优惠劵不存在或者已经过期");
        // 获取优惠券限制条件
        List<ShopCouponApplyRanges> shopCouponApplyRangesList = shopCouponApplyRangesMapper.selectList(
                new LambdaQueryWrapper<ShopCouponApplyRanges>()
                        .eq(ShopCouponApplyRanges::getCouponId, request.getCouponId())
        );
        // 获取符合条件的分类ID
        HashSet<Long> couponApplyCategoryIds = (HashSet<Long>) shopCouponApplyRangesList.stream().filter(
                couponApplyRanges -> couponApplyRanges.getScopeType() == ShopCouponApplyRangesEnums.ScopeType.PRODUCT_CATEGORY.getId()
        ).map(couponApplyRanges -> couponApplyRanges.getScopeId()).collect(Collectors.toSet());
        // 获取符合条件的商品ID
        HashSet<Long> couponApplyProductIds = (HashSet<Long>) shopCouponApplyRangesList.stream().filter(
                couponApplyRanges -> couponApplyRanges.getScopeType() == ShopCouponApplyRangesEnums.ScopeType.COMMODITY.getId()
        ).map(couponApplyRanges -> couponApplyRanges.getScopeId()).collect(Collectors.toSet());

        // 获取符合条件的商品金额
        BigDecimal couponProductTotalAmount = BigDecimal.ZERO;
        for(ProductSkuVo product : request.getProducts()) {
            ShopProductInfo productInfo = shopProductInfoMapper.selectById(product.getProductId());
            Assert.notNull(productInfo, "商品不存在");

            // 非全平台，店铺ID对不上直接跳过
            if(shopCoupons.getApplyScope() != ShopCouponsEnums.ApplyScope.ALL_PLATFORMS.getId() &&
                !productInfo.getShopId().equals(shopCoupons.getShopId())
            ) {
                continue;
            }

            if(shopCoupons.getApplyScope() == ShopCouponsEnums.ApplyScope.ALL_PLATFORMS.getId()) {
                // 全平台
                couponProductTotalAmount = couponProductTotalAmount.add(product.getTotalAmount());
            } else if(shopCoupons.getApplyScope() == ShopCouponsEnums.ApplyScope.SPECIFY_CLASSIFICATION.getId()) {
                // 指定分类商品
                if(couponApplyCategoryIds.contains(productInfo.getCategoryId())) {
                    couponProductTotalAmount = couponProductTotalAmount.add(product.getTotalAmount());
                }
            } else if(shopCoupons.getApplyScope() == ShopCouponsEnums.ApplyScope.SPECIFIED_GOODS.getId()) {
                // 指定商品
                if(couponApplyProductIds.contains(productInfo.getId())) {
                    couponProductTotalAmount = couponProductTotalAmount.add(product.getTotalAmount());
                }
            } else if(shopCoupons.getApplyScope() == ShopCouponsEnums.ApplyScope.WHOLE_STORE.getId()) {
                // 店铺商品
                if(productInfo.getShopId().equals(shopCoupons.getShopId())) {
                    couponProductTotalAmount = couponProductTotalAmount.add(product.getTotalAmount());
                }
            }
        }

        // 计算优惠金额
        BigDecimal discountAmount = BigDecimal.ZERO;
        // 满额减计算
        if(shopCoupons.getType() == ShopCouponsEnums.Type.FULL_REDUCTION.getId()) {
            if(couponProductTotalAmount.compareTo(shopCoupons.getMinOrderAmount()) >= 0) {
                discountAmount = couponProductTotalAmount.subtract(shopCoupons.getDiscountValue());
            }
        } else if(shopCoupons.getType() == ShopCouponsEnums.Type.FULL_DISCOUNT.getId()) {
            discountAmount = couponProductTotalAmount.multiply(shopCoupons.getDiscountValue());
            if(discountAmount.compareTo(shopCoupons.getMaxDiscountAmount()) > 0) {
                discountAmount = shopCoupons.getMaxDiscountAmount();
            }
        } else if(shopCoupons.getType() == ShopCouponsEnums.Type.FREE_SHIPPING.getId()) {
            // 免运费
            discountAmount = request.getDeliveryFee();
        }

        // 如果没有优惠；则说明优惠劵没有优惠
        if(discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        // 创建优惠劵使用记录
        ShopCouponUsageLogs shopCouponUsageLogs = new ShopCouponUsageLogs();
        shopCouponUsageLogs.setUserId(Long.valueOf(request.getUserId()))
                .setCouponId(Long.valueOf(request.getCouponId()))
//                .setOrderId(shopOrderInfo.getId())
                .setOrderAmount(couponProductTotalAmount)
                .setDiscountAmount(discountAmount)
                .setUsedAt(LocalDateTime.now());

        return shopCouponUsageLogs;
    }
}