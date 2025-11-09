package work.soho.shop.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.core.util.BeanUtils;
import work.soho.shop.api.request.OrderCreateRequest;
import work.soho.shop.api.vo.OrderDetailsVo;
import work.soho.shop.api.vo.ProductSkuVo;
import work.soho.shop.biz.domain.ShopOrderInfo;
import work.soho.shop.biz.domain.ShopProductFreight;
import work.soho.test.TestApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
//@ComponentScan(basePackages = {"work.soho.shop.biz.aspect", "work.soho.shop.biz"}) // 添加切面包扫描
class ShopOrderInfoServiceTest {

    @Autowired
    private ShopOrderInfoService shopOrderInfoService;

    @Autowired
    private ShopProductFreightService shopProductFreightService;

    @Test
    void createOrder() {
        System.out.println("createOrder");
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(1L);
        request.setUserAddressId(1);
        request.setUserCouponId(null);
        request.setRemark("备注");
        request.setType(1);
        request.setSource(1);
        request.setDeliveryFee(BigDecimal.valueOf(15.00));
        request.setDeliveryTime("2023-05-01 00:00:00");

        // 添加商品
        List<ProductSkuVo> products = new ArrayList<>();
        ProductSkuVo product = new ProductSkuVo();
        product.setProductId(1L);
        product.setSkuId(7);
        product.setName("商品1");
        product.setMainImage("https://www.baidu.com");
        product.setAmount(BigDecimal.valueOf(100.00));
        product.setQty(2);
        product.setTotalAmount(BigDecimal.valueOf(100.00));
        products.add(product);
        request.setProducts(products);



        ShopProductFreight productFreight = shopProductFreightService.getOne(new LambdaQueryWrapper<ShopProductFreight>()
                .eq(ShopProductFreight::getProductId, product.getProductId())
        );

        ShopProductFreight changeProductFreight = null;
        ShopOrderInfo order = null;

//        // 按重量不续重测试
//        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
//        changeProductFreight.setWeight(new BigDecimal("1.000"))
//                .setLength(new BigDecimal("1.000"))
//                .setWidth(new BigDecimal("1.000"))
//                .setHeight(new BigDecimal("1.000"))
//                .setVolumetricWeight(new BigDecimal("1.000"))
//                .setTemplateId(2L)  // 按重量计算运费
//        ;
//        shopProductFreightService.updateById(changeProductFreight);
//        order = shopOrderInfoService.createOrder( request);
//        assertTrue(new BigDecimal("1.00").compareTo(order.getDeliveryFee()) == 0);
//
//
//        // 测试免运费
//        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
//        changeProductFreight.setWeight(new BigDecimal("1.000"))
//                .setLength(new BigDecimal("1.000"))
//                .setWidth(new BigDecimal("1.000"))
//                .setHeight(new BigDecimal("1.000"))
//                .setVolumetricWeight(new BigDecimal("1.000"))
//                .setTemplateId(1L)  // 按重量计算运费
//        ;
//        shopProductFreightService.updateById(changeProductFreight);
//        order = shopOrderInfoService.createOrder( request);
//        assertTrue(new BigDecimal("0.00").compareTo(order.getDeliveryFee()) == 0);


//        // 按重量计算运费 续费测试
//        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
//        changeProductFreight.setWeight(new BigDecimal("8.000"))
//                .setLength(new BigDecimal("1.000"))
//                .setWidth(new BigDecimal("1.000"))
//                .setHeight(new BigDecimal("1.000"))
//                .setVolumetricWeight(new BigDecimal("1.000"))
//                .setTemplateId(2L)  // 按重量计算运费
//        ;
//        shopProductFreightService.updateById(changeProductFreight);
//
//        order = shopOrderInfoService.createOrder( request);
//        assertTrue(new BigDecimal("10.00").compareTo(order.getDeliveryFee()) == 0);


        // 按体积计算
//        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
//        changeProductFreight.setWeight(new BigDecimal("8.000"))
//                .setLength(new BigDecimal("1.000"))
//                .setWidth(new BigDecimal("1.000"))
//                .setHeight(new BigDecimal("8.000"))
//                .setVolumetricWeight(new BigDecimal("1.000"))
//                .setTemplateId(3L)  // 按重量计算运费
//        ;
//        shopProductFreightService.updateById(changeProductFreight);
//        order = shopOrderInfoService.createOrder( request);
//        assertTrue(new BigDecimal("14.00").compareTo(order.getDeliveryFee()) == 0);


//        // 按照商品数量计数
//        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
//        changeProductFreight.setWeight(new BigDecimal("8.000"))
//                .setLength(new BigDecimal("1.000"))
//                .setWidth(new BigDecimal("1.000"))
//                .setHeight(new BigDecimal("8.000"))
//                .setVolumetricWeight(new BigDecimal("1.000"))
//                .setTemplateId(4L)  // 按重量计算运费
//        ;
//        shopProductFreightService.updateById(changeProductFreight);
//        order = shopOrderInfoService.createOrder( request);
//        assertTrue(new BigDecimal("5.00").compareTo(order.getDeliveryFee()) == 0);


        // 测试订单优惠劵功能
        request.setUserCouponId(1L);
        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
        changeProductFreight.setWeight(new BigDecimal("8.000"))
                .setLength(new BigDecimal("1.000"))
                .setWidth(new BigDecimal("1.000"))
                .setHeight(new BigDecimal("8.000"))
                .setVolumetricWeight(new BigDecimal("1.000"))
                .setTemplateId(4L)  // 按重量计算运费
        ;
        shopProductFreightService.updateById(changeProductFreight);
        order = shopOrderInfoService.createOrder( request);
        assertTrue(new BigDecimal("5.00").compareTo(order.getDeliveryFee()) == 0);



        // 恢复测试影响
        shopProductFreightService.updateById(productFreight);
    }

    @Test
    void createOrder2() {
        System.out.println("createOrder");
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(1L);
        request.setUserAddressId(1);
        request.setUserCouponId(null);
        request.setRemark("备注");
        request.setType(1);
        request.setSource(1);
        request.setDeliveryFee(BigDecimal.valueOf(15.00));
        request.setDeliveryTime("2023-05-01 00:00:00");

        // 添加商品
        List<ProductSkuVo> products = new ArrayList<>();
        ProductSkuVo product = new ProductSkuVo();
        product.setProductId(1L);
        product.setSkuId(7);
        product.setName("商品1");
        product.setMainImage("https://www.baidu.com");
        product.setAmount(BigDecimal.valueOf(100.00));
        product.setQty(2);
        product.setTotalAmount(BigDecimal.valueOf(100.00));
        products.add(product);
        request.setProducts(products);



        ShopProductFreight productFreight = shopProductFreightService.getOne(new LambdaQueryWrapper<ShopProductFreight>()
                .eq(ShopProductFreight::getProductId, product.getProductId())
        );

        ShopProductFreight changeProductFreight = null;
        OrderDetailsVo order = null;

//        // 按重量不续重测试
//        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
//        changeProductFreight.setWeight(new BigDecimal("1.000"))
//                .setLength(new BigDecimal("1.000"))
//                .setWidth(new BigDecimal("1.000"))
//                .setHeight(new BigDecimal("1.000"))
//                .setVolumetricWeight(new BigDecimal("1.000"))
//                .setTemplateId(2L)  // 按重量计算运费
//        ;
//        shopProductFreightService.updateById(changeProductFreight);
        order = shopOrderInfoService.calculationOrder( request);
        System.out.println(order);
//        assertTrue(new BigDecimal("1.00").compareTo(order.getDeliveryFee()) == 0);


        // 测试订单优惠劵功能
//        request.setUserCouponId(1L);
//        changeProductFreight = BeanUtils.copy(productFreight, ShopProductFreight.class);
//        changeProductFreight.setWeight(new BigDecimal("8.000"))
//                .setLength(new BigDecimal("1.000"))
//                .setWidth(new BigDecimal("1.000"))
//                .setHeight(new BigDecimal("8.000"))
//                .setVolumetricWeight(new BigDecimal("1.000"))
//                .setTemplateId(4L)  // 按重量计算运费
//        ;
//        shopProductFreightService.updateById(changeProductFreight);
//        order = shopOrderInfoService.createOrder( request);
//        assertTrue(new BigDecimal("5.00").compareTo(order.getDeliveryFee()) == 0);
//
//
//
//        // 恢复测试影响
//        shopProductFreightService.updateById(productFreight);
    }
}