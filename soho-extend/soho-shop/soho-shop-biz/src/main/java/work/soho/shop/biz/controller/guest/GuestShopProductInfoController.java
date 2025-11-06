package work.soho.shop.biz.controller.guest;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.shop.api.vo.ProductDetailsVo;
import work.soho.shop.biz.domain.*;
import work.soho.shop.biz.service.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

;
/**
 * 商品信息Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/guest/shopProductInfo" )
public class GuestShopProductInfoController {

    private final ShopProductInfoService shopProductInfoService;
    private final ShopProductSkuService shopProductSkuService;
    private final ShopProductSpecValueService shopProductSpecValueService;
    private final ShopProductFreightService shopProductFreightService;
    private final ShopProductModelSpecService shopProductModelSpecService;
    private final ShopProductCategoryService shopProductCategoryService;

    /**
     * 查询商品信息列表
     */
    @GetMapping("/list")
    @Node(value = "guest::shopProductInfo::list", name = "获取 商品信息 列表")
    public R<PageSerializable<ShopProductInfo>> list(ShopProductInfo shopProductInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductInfo> lqw = new LambdaQueryWrapper<ShopProductInfo>();
        lqw.eq(shopProductInfo.getId() != null, ShopProductInfo::getId ,shopProductInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSpuCode()),ShopProductInfo::getSpuCode ,shopProductInfo.getSpuCode());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getName()),ShopProductInfo::getName ,shopProductInfo.getName());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSubTitle()),ShopProductInfo::getSubTitle ,shopProductInfo.getSubTitle());
        lqw.like(shopProductInfo.getQty() != null,ShopProductInfo::getQty ,shopProductInfo.getQty());
        lqw.eq(shopProductInfo.getOriginalPrice() != null, ShopProductInfo::getOriginalPrice ,shopProductInfo.getOriginalPrice());
        lqw.eq(shopProductInfo.getSellingPrice() != null, ShopProductInfo::getSellingPrice ,shopProductInfo.getSellingPrice());
        lqw.eq(shopProductInfo.getShopId() != null, ShopProductInfo::getShopId ,shopProductInfo.getShopId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getMainImage()),ShopProductInfo::getMainImage ,shopProductInfo.getMainImage());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getThumbnails()),ShopProductInfo::getThumbnails ,shopProductInfo.getThumbnails());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getDetailHtml()),ShopProductInfo::getDetailHtml ,shopProductInfo.getDetailHtml());
        lqw.eq(shopProductInfo.getCommentCount() != null, ShopProductInfo::getCommentCount ,shopProductInfo.getCommentCount());
        lqw.like(shopProductInfo.getCategoryId() != null,ShopProductInfo::getCategoryId ,shopProductInfo.getCategoryId());
        lqw.like(shopProductInfo.getShelfStatus() != null,ShopProductInfo::getShelfStatus ,shopProductInfo.getShelfStatus());
        lqw.eq(shopProductInfo.getAuditStatus() != null, ShopProductInfo::getAuditStatus ,shopProductInfo.getAuditStatus());
        lqw.eq(shopProductInfo.getUpdatedTime() != null, ShopProductInfo::getUpdatedTime ,shopProductInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ShopProductInfo> list = shopProductInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::shopProductInfo::getInfo", name = "获取 商品信息 详细信息")
    public R<ProductDetailsVo> getDetails(@PathVariable("id" ) Long id) {
        ShopProductInfo shopProductInfo = shopProductInfoService.getById(id);

        Assert.notNull(shopProductInfo, "商品信息不存在");

        ProductDetailsVo productDetailsVo = BeanUtils.copy(shopProductInfo, ProductDetailsVo.class);
        // 查询sku信息
        List<ShopProductSku> skuList = shopProductSkuService.list(new LambdaQueryWrapper<ShopProductSku>().eq(ShopProductSku::getProductId, id));
        List<ProductDetailsVo.SkuInfo> skus = skuList.stream().map(sku -> BeanUtils.copy(sku, ProductDetailsVo.SkuInfo.class)).collect(Collectors.toList());

        // 生成sku map信息
        HashMap<String, ProductDetailsVo.SkuInfo> skusMap = new HashMap<>();
        skus.forEach(sku -> {
            List<ShopProductSpecValue> specValues = shopProductSpecValueService.list(
                    new LambdaQueryWrapper<ShopProductSpecValue>()
                            .eq(ShopProductSpecValue::getSkuId, sku.getId())
                            .orderByDesc(ShopProductSpecValue::getSortOrder));
            String key = "";
            for(ShopProductSpecValue specValue: specValues) {
                if(!key.equals("")) {
                    key += ";";
                }
                key += specValue.getSpecId() + ":" + specValue.getValue();
            }
            skusMap.put(key, sku);
        });
        productDetailsVo.setSkus(skusMap);

        // 获取商品规格信息
        List<ShopProductSpecValue> specList = shopProductSpecValueService.list(
                new LambdaQueryWrapper<ShopProductSpecValue>().eq(ShopProductSpecValue::getProductId, id)
                        .eq(ShopProductSpecValue::getSkuId, 0)
//                        .isNull(ShopProductSpecValue::getSkuId)
                        .orderByDesc(ShopProductSpecValue::getSortOrder)
        );

        Map<Integer, String> specs = specList.stream().collect(Collectors.toMap(ShopProductSpecValue::getSpecId, ShopProductSpecValue::getValue));
        productDetailsVo.setSpecs(specs);

        // 获取商品运费信息
        ShopProductFreight shopProductFreight = shopProductFreightService.getOne(new LambdaQueryWrapper<ShopProductFreight>().eq(ShopProductFreight::getProductId, id));
        if(shopProductFreight != null) {
            ProductDetailsVo.FeightInfo feightInfo = BeanUtils.copy(shopProductFreight, ProductDetailsVo.FeightInfo.class);
            productDetailsVo.setFeightInfo(feightInfo);
        }

        // 获取规格名称信息
        ShopProductCategory shopProductCategory = shopProductCategoryService.getById(shopProductInfo.getCategoryId());
        Assert.notNull(shopProductCategory, "商品分类不存在");
        List<ShopProductModelSpec> modelSpecs = shopProductModelSpecService.list(
                new LambdaQueryWrapper<ShopProductModelSpec>()
                        .eq(ShopProductModelSpec::getModelId, shopProductCategory.getModelId()));
        Map<Integer, String> specsNames = modelSpecs.stream().collect(Collectors.toMap(ShopProductModelSpec::getId, ShopProductModelSpec::getName));
        productDetailsVo.setSpecsNames(specsNames);
        return R.success(productDetailsVo);
    }

    /**
     * 新增商品信息
     */
    @PostMapping
    @Node(value = "guest::shopProductInfo::add", name = "新增 商品信息")
    public R<Boolean> add(@RequestBody ShopProductInfo shopProductInfo) {
        return R.success(shopProductInfoService.save(shopProductInfo));
    }

    /**
     * 修改商品信息
     */
    @PutMapping
    @Node(value = "guest::shopProductInfo::edit", name = "修改 商品信息")
    public R<Boolean> edit(@RequestBody ShopProductInfo shopProductInfo) {
        return R.success(shopProductInfoService.updateById(shopProductInfo));
    }

    /**
     * 删除商品信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "guest::shopProductInfo::remove", name = "删除 商品信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductInfoService.removeByIds(Arrays.asList(ids)));
    }
}