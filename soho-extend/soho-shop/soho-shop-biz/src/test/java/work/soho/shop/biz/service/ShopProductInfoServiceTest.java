package work.soho.shop.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.shop.biz.domain.ShopProductInfo;
import work.soho.shop.biz.domain.ShopProductSku;
import work.soho.shop.biz.domain.ShopProductSpecValue;
import work.soho.test.TestApp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest( classes = TestApp.class )
@ActiveProfiles("dev")
@Log4j2
class ShopProductInfoServiceTest {
    @Autowired
    private ShopProductInfoService shopProductInfoService;
    @Autowired
    private ShopProductSkuService shopProductSkuService;
    @Autowired
    private ShopProductSpecValueService shopProductSpecValueService;

    private ShopProductInfo crateProductByTemplate(Long id) {
        ShopProductInfo templateProduct = shopProductInfoService.getById(id);
        templateProduct.setId(null);
        shopProductInfoService.save(templateProduct);
        List<ShopProductSku> skuList = shopProductSkuService.list(new LambdaQueryWrapper<ShopProductSku>()
                .eq(ShopProductSku::getProductId, id));

        List<ShopProductSpecValue> specValueList = shopProductSpecValueService.list(new LambdaQueryWrapper<ShopProductSpecValue>()
                .eq(ShopProductSpecValue::getProductId, id)
        );
        Map<Integer, List<ShopProductSpecValue>> specValueMap = specValueList.stream().collect(
                Collectors.groupingBy(ShopProductSpecValue::getSkuId)
        );

        // 保存商品属性值
        List<ShopProductSpecValue> productSpecValueList = specValueMap.get(0);
        if(productSpecValueList != null) {
            productSpecValueList.forEach(specValue -> {
                specValue.setId(null);
                specValue.setProductId(templateProduct.getId());
                shopProductSpecValueService.save(specValue);
            });
        }

        skuList.forEach(sku -> {
            Integer oldSkuId = sku.getId();
            sku.setId(null);
            sku.setProductId(templateProduct.getId());
            shopProductSkuService.save(sku);

            List<ShopProductSpecValue> skuSpecValueList = specValueMap.get(oldSkuId);
            if(skuSpecValueList != null) {
                skuSpecValueList.forEach(specValue -> {
                    specValue.setId(null);
                    specValue.setProductId(templateProduct.getId());
                    specValue.setSkuId(sku.getId());
                    shopProductSpecValueService.save(specValue);
                });
            }
        });

        return templateProduct;
    }

    @Test
    public void testDelete() {
        // 以第一个商品为模板创建两个产品
        ShopProductInfo templateProduct = crateProductByTemplate(1L);
        System.out.println(templateProduct);

        shopProductInfoService.removeById(templateProduct.getId());
    }
}