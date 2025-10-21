package work.soho.shop.biz.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.shop.api.request.OrderCreateRequest;
import work.soho.shop.api.vo.ProductSkuVo;
import work.soho.test.TestApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@Log4j2
//@ComponentScan(basePackages = {"work.soho.shop.biz.aspect", "work.soho.shop.biz"}) // 添加切面包扫描
class ShopOrderInfoServiceTest {

    @Autowired
    private ShopOrderInfoService shopOrderInfoService;

    @Test
    void createOrder() {
        System.out.println("createOrder");
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(1);
        request.setUserAddressId(1);
        request.setCouponId(null);
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

        shopOrderInfoService.createOrder( request);
    }
}