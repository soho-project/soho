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
import work.soho.shop.biz.enums.*;
import work.soho.shop.biz.mapper.*;
import work.soho.shop.biz.service.ShopInfoService;
import work.soho.shop.biz.service.ShopOrderInfoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
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
    private final ShopProductFreightMapper shopProductFreightMapper;
    private final ShopFreightTemplateMapper shopFreightTemplateMapper;
    private final ShopFreightRuleMapper shopFreightRuleMapper;

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

        // 计算订单运费
        BigDecimal deliveryFee = getFreightAmount(shopOrderSkuList, shopUserAddresses);
        shopOrderInfo.setDeliveryFee(deliveryFee);

        // 优惠劵计算
        if(request.getCouponId() != null) {
            ShopCouponUsageLogs shopCouponUsageLogs = createCouponUsageLogs(request);
            if(shopCouponUsageLogs != null) {
                shopOrderInfo.setDiscountAmount(shopCouponUsageLogs.getDiscountAmount());
            }
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

    /**
     * 获取运费金额
     *
     * @param shopOrderSkus
     * @return
     */
    private BigDecimal getFreightAmount(List<ShopOrderSku> shopOrderSkus, ShopUserAddresses shopUserAddresses) {
        List<Long> productIds = shopOrderSkus.stream().map(shopOrderSku -> shopOrderSku.getProductId()).collect(Collectors.toList());
        // 对应商品的运费信息
        List<ShopProductFreight> shopProductFreights = shopProductFreightMapper.selectList(
                new LambdaQueryWrapper<ShopProductFreight>()
                .in(ShopProductFreight::getProductId, productIds)
        );
        // 运费模板id
        List<Long> shopFreightTemplateIds = shopProductFreights.stream().map(shopProductFreight -> shopProductFreight.getTemplateId()).collect(Collectors.toList());
        // 获取模板信息
        List<ShopFreightTemplate> shopFreightTemplates = shopFreightTemplateMapper.selectBatchIds(shopFreightTemplateIds);
        Map<Long, ShopFreightTemplate> shopFreightTemplateMap = shopFreightTemplates.stream().collect(Collectors.toMap(ShopFreightTemplate::getId, shopFreightTemplate -> shopFreightTemplate));
        // 分模板计算运费
        Map<Long, List<ShopProductFreight>> shopProductFreightMap = shopProductFreights.stream().collect(Collectors.groupingBy(ShopProductFreight::getTemplateId));

        BigDecimal freightAmount = BigDecimal.ZERO;

        for(Long shopFreightTemplateId : shopProductFreightMap.keySet()) {
            List<ShopProductFreight> productFreights = shopProductFreightMap.get(shopFreightTemplateId);
            ShopFreightTemplate shopFreightTemplate = shopFreightTemplateMap.get(shopFreightTemplateId);
            List<Long> templateProductIds = productFreights.stream().map(productFreight -> productFreight.getProductId()).collect(Collectors.toList());
            List<ShopOrderSku> templateOrderSkus = shopOrderSkus.stream().filter(shopOrderSku -> templateProductIds.contains(shopOrderSku.getProductId())).collect(Collectors.toList());
            Map<Long, ShopProductFreight> templateProductFreightMap = productFreights.stream().collect(Collectors.toMap(ShopProductFreight::getProductId, shopProductFreight -> shopProductFreight));

            // 检查商品是否符合免运费条件; 如果符合免运费条件直接跳过
            if(shopFreightTemplate.getIsFreeShipping() == ShopFreightTemplateEnums.IsFreeShipping.YES.getId()) {
                // 检查免运费条件
                if(shopFreightTemplate.getFreeConditionType() == ShopFreightTemplateEnums.FreeConditionType.BY_NO.getId()) {
                    continue;
                } else if(shopFreightTemplate.getFreeConditionType() == ShopFreightTemplateEnums.FreeConditionType.BY_AMOUNT.getId()) {
                    // 按照商品金额免运费
                    // 获取该模板下商品的金额
                    BigDecimal productAmount = templateOrderSkus.stream()
                            .map(shopOrderSku -> shopOrderSku.getTotalAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if(productAmount.compareTo(shopFreightTemplate.getFreeConditionValue()) >= 0) {
                        // 符合免邮条件
                        continue;
                    }
                } else if(shopFreightTemplate.getFreeConditionType() == ShopFreightTemplateEnums.FreeConditionType.BY_QUANTITY.getId()) {
                    // 按照件数免运费
                    // 获取该运费模板下面的商品数量
                    int productQuantity = templateOrderSkus.stream().map(shopOrderSku -> shopOrderSku.getQty()).reduce(0, Integer::sum);
                    if(BigDecimal.valueOf(productQuantity).compareTo(shopFreightTemplate.getFreeConditionValue())>=0) {
                        // 符合免邮条件
                        continue;
                    }
                } else if(shopFreightTemplate.getFreeConditionType() == ShopFreightTemplateEnums.FreeConditionType.BY_WEIGHT.getId()) {
                    // 按照重量免运费
                    BigDecimal productWeight = templateOrderSkus.stream().map(shopOrderSku ->
                            templateProductFreightMap.get(shopOrderSku.getProductId()).getWeight().multiply(BigDecimal.valueOf(shopOrderSku.getQty()))
                    ).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if(productWeight.compareTo(shopFreightTemplate.getFreeConditionValue())>=0) {
                        // 符合免邮条件
                        continue;
                    }
                }
            }

            // 获取运费模板规则
            List<ShopFreightRule> shopFreightRules = shopFreightRuleMapper.selectList(new LambdaQueryWrapper<ShopFreightRule>().eq(ShopFreightRule::getTemplateId, shopFreightTemplateId));
            // 查找默认货运规则
            ShopFreightRule defaultShopFreightRule = shopFreightRules.stream().filter(shopFreightRule -> shopFreightRule.getIsDefault() == 1).findFirst().orElse(null);
            // 根据地区码匹配运费规则
            ShopFreightRule currentShopFreightRule = shopFreightRules.stream().filter(shopFreightRule -> {
                List<String> areaCodes = Arrays.stream(shopFreightRule.getRegionCodes().split(",")).collect(Collectors.toList());
                return areaCodes.contains(shopUserAddresses.getDistrict());
            }).findFirst().orElse(defaultShopFreightRule);

            Assert.notNull(currentShopFreightRule, "没有匹配的运费规则");


            // 获取运费计算值
            BigDecimal value = BigDecimal.ZERO;

            if(shopFreightTemplate.getType() == ShopFreightTemplateEnums.Type.BY_QUANTITY.getId()) {
                // 按数量
                value = BigDecimal.valueOf(templateOrderSkus.stream().map(shopOrderSku -> shopOrderSku.getQty()).reduce(0, Integer::sum));
            } else if(shopFreightTemplate.getType() == ShopFreightTemplateEnums.Type.BY_WEIGHT.getId()) {
                // 按照重量计算
                value = templateOrderSkus.stream().map(shopOrderSku ->
                        templateProductFreightMap.get(shopOrderSku.getProductId()).getWeight().multiply(BigDecimal.valueOf(shopOrderSku.getQty()))
                ).reduce(BigDecimal.ZERO, BigDecimal::add);
            } else if (shopFreightTemplate.getType() == ShopFreightTemplateEnums.Type.BY_VOLUME.getId()) {
                // 按体积
                value = templateOrderSkus.stream().map(shopOrderSku ->{
                    ShopProductFreight shopProductFreight = templateProductFreightMap.get(shopOrderSku.getProductId());
                    return shopProductFreight.getWidth().multiply(shopProductFreight.getLength()).multiply(shopProductFreight.getHeight()).multiply(BigDecimal.valueOf(shopOrderSku.getQty()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
            } else if (shopFreightTemplate.getType() == ShopFreightTemplateEnums.Type.FLAT_SHIPPING_RATE.getId()) {
                // 固定运费;  直接加上一个固定值无须继续计算了
                // nothing
                freightAmount = freightAmount.add(currentShopFreightRule.getFirstUnitPrice());
                continue;
            }

            // 计算运费
            if(value.compareTo(currentShopFreightRule.getFirstUnit())>=0) {
                freightAmount = freightAmount.add(currentShopFreightRule.getFirstUnitPrice());
                value = value.subtract(currentShopFreightRule.getFirstUnit());
            }

            int number = (int)Math.ceil(value.divide(currentShopFreightRule.getContinueUnit(), RoundingMode.UP).doubleValue());
            freightAmount = freightAmount.add(currentShopFreightRule.getContinueUnitPrice().multiply(new BigDecimal(number)));

        }

        return freightAmount;
    }
}