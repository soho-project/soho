package work.soho.shop.biz.controller;

import cn.hutool.core.lang.Assert;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.api.request.ProductInfoRequest;
import work.soho.shop.api.vo.ProductDetailsVo;
import work.soho.shop.biz.domain.*;
import work.soho.shop.biz.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品信息Controller
 *
 * @author fang
 */
@Api(tags = "商品信息")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductInfo" )
public class ShopProductInfoController {

    private final ShopProductInfoService shopProductInfoService;

    private final ShopProductSkuService shopProductSkuService;

    private final ShopProductSpecValueService shopProductSpecValueService;

    private final ShopProductModelService shopProductModelService;

    private final ShopProductModelSpecService shopProductModelSpecService;

    private final ShopProductCategoryService shopProductCategoryService;

    private final ShopProductFreightService shopProductFreightService;

    /**
     * 查询商品信息列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductInfo::list", name = "获取 商品信息 列表")
    public R<PageSerializable<ShopProductInfo>> list(ShopProductInfo shopProductInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductInfo.getId() != null, ShopProductInfo::getId ,shopProductInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSpuCode()),ShopProductInfo::getSpuCode ,shopProductInfo.getSpuCode());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getName()),ShopProductInfo::getName ,shopProductInfo.getName());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSubTitle()),ShopProductInfo::getSubTitle ,shopProductInfo.getSubTitle());
//        lqw.like(StringUtils.isNotBlank(shopProductInfo.getQty()),ShopProductInfo::getQty ,shopProductInfo.getQty());
        lqw.eq(shopProductInfo.getOriginalPrice() != null, ShopProductInfo::getOriginalPrice ,shopProductInfo.getOriginalPrice());
        lqw.eq(shopProductInfo.getSellingPrice() != null, ShopProductInfo::getSellingPrice ,shopProductInfo.getSellingPrice());
        lqw.eq(shopProductInfo.getShopId() != null, ShopProductInfo::getShopId ,shopProductInfo.getShopId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getMainImage()),ShopProductInfo::getMainImage ,shopProductInfo.getMainImage());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getThumbnails()),ShopProductInfo::getThumbnails ,shopProductInfo.getThumbnails());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getDetailHtml()),ShopProductInfo::getDetailHtml ,shopProductInfo.getDetailHtml());
        lqw.eq(shopProductInfo.getCommentCount() != null, ShopProductInfo::getCommentCount ,shopProductInfo.getCommentCount());
        lqw.in(shopProductInfo.getCategoryId()!=null,
                ShopProductInfo::getCategoryId ,
                shopProductCategoryService.getCategoryIdsById(shopProductInfo.getCategoryId()));
        lqw.like(shopProductInfo.getShelfStatus() != null,ShopProductInfo::getShelfStatus ,shopProductInfo.getShelfStatus());
        lqw.eq(shopProductInfo.getAuditStatus() != null, ShopProductInfo::getAuditStatus ,shopProductInfo.getAuditStatus());
        lqw.eq(shopProductInfo.getUpdatedTime() != null, ShopProductInfo::getUpdatedTime ,shopProductInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductInfo::getId);
        List<ShopProductInfo> list = shopProductInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductInfo::getInfo", name = "获取 商品信息 详细信息")
    public R<ShopProductInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductInfoService.getById(id));
    }

    /**
     * 获取商品信息详细信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/details/{id}" )
    @Node(value = "shopProductInfo::getDetailsInfo", name = "获取 商品信息 详细信息（sku）")
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

        return R.success(productDetailsVo);
    }

    /**
     * 新增商品信息
     */
    @PostMapping("update")
    @Node(value = "shopProductInfo::update", name = "新增、编辑 商品信息")
    public R<Boolean> update(@RequestBody ProductInfoRequest productInfoRequest) {
        DynamicDataSourceContextHolder.push("shop");
        ShopProductInfo shopProductInfo = BeanUtils.copy(productInfoRequest, ShopProductInfo.class);
        // 检查商code是否存在
        if(StringUtils.isBlank(shopProductInfo.getSpuCode())) {
            shopProductInfo.setSpuCode(IDGeneratorUtils.snowflake().toString());
        }
        shopProductInfoService.saveOrUpdate(shopProductInfo);

        // 保存sku
        Map<String, ProductInfoRequest.SkuInfo> skusMap = productInfoRequest.getSkus();
        List<ProductInfoRequest.SkuInfo> skus = skusMap.values().stream().collect(Collectors.toList());
        // 获取数据库存在的codes
        List<String> codes = shopProductSkuService.list(new LambdaQueryWrapper<ShopProductSku>().eq(ShopProductSku::getCode, productInfoRequest.getId()))
                .stream().map(ShopProductSku::getCode).collect(Collectors.toList());
        // 获取数据库不存在的codes; 新增的codes
        List<String> notInCodes = skus.stream().filter(item -> !codes.contains(item.getCode())).map(item -> item.getCode()).collect(Collectors.toList());
        // 获取需要删除的 codes
        List<String> deleteCodes = codes.stream().filter(item -> !skus.stream().map(sku -> sku.getCode()).collect(Collectors.toList()).contains(item)).collect(Collectors.toList());

        // 获取商品属性对应的模型排序信息
        ShopProductCategory shopProductCategory = shopProductCategoryService.getById(productInfoRequest.getCategoryId());
        List<ShopProductModelSpec> specs = shopProductModelSpecService.list(new LambdaQueryWrapper<ShopProductModelSpec>().eq(ShopProductModelSpec::getModelId, shopProductCategory.getModelId()));
        // 整理specId,sort_order键值对
        HashMap<Integer, Integer> specIdSortOrderMap = new HashMap<>();
        specs.forEach(item -> specIdSortOrderMap.put(item.getId(), item.getSortOrder()));

        // 处理sku 属性
        for (String key : skusMap.keySet()) {
            ProductInfoRequest.SkuInfo item = skusMap.get(key);
            // Fixe bug 新增商品的时候  人为指定sku id 导致脏数据； 检查sku是否为当前商品的sku
            if(item.getId() != null) {
                ShopProductSku sku = shopProductSkuService.getById(item.getId());
                Assert.isTrue(sku.getProductId().equals(shopProductInfo.getId()), "商品信息不存在");
            }

            ShopProductSku shopProductSku = BeanUtils.copy(item, ShopProductSku.class);
            shopProductSku.setProductId(shopProductInfo.getId());
            // 检查sku code 是否存在
            if(StringUtils.isBlank(shopProductSku.getCode())) {
                shopProductSku.setCode(IDGeneratorUtils.snowflake().toString());
            }
            shopProductSkuService.saveOrUpdate(shopProductSku);

            // 拆分sku属性 1:黑色;2:4G
            List<HashMap<Integer, String>> skuAttrs = new ArrayList<>();
            for (String attr : key.split(";")) {
                HashMap<Integer, String> attrMap = new HashMap<>();
                String[] attrs = attr.split(":");
                attrMap.put(Integer.parseInt(attrs[0]), attrs[1]);
                skuAttrs.add(attrMap);
            }

            // 进行属性存储
            for (HashMap<Integer, String> attr : skuAttrs) {
                for(Integer specId : attr.keySet()) {
                    // 先查询是否已存在记录
                    ShopProductSpecValue existingValue = shopProductSpecValueService.getOne(
                            new LambdaQueryWrapper<ShopProductSpecValue>()
                                    .eq(ShopProductSpecValue::getProductId, shopProductInfo.getId())
                                    .eq(ShopProductSpecValue::getSkuId, shopProductSku.getId())
                                    .eq(ShopProductSpecValue::getSpecId, specId)
                    );

                    ShopProductSpecValue shopProductSpecValue;
                    if (existingValue != null) {
                        // 如果存在，则更新现有记录
                        shopProductSpecValue = existingValue;
                        shopProductSpecValue.setValue(attr.get(specId));
                        shopProductSpecValue.setSortOrder(specIdSortOrderMap.get(specId));
                    } else {
                        // 如果不存在，则创建新记录
                        shopProductSpecValue = new ShopProductSpecValue();
                        shopProductSpecValue.setProductId(shopProductInfo.getId());
                        shopProductSpecValue.setSkuId(shopProductSku.getId());
                        shopProductSpecValue.setSpecId(specId);
                        shopProductSpecValue.setValue(attr.get(specId));
                        shopProductSpecValue.setSortOrder(specIdSortOrderMap.get(specId));
                    }
                    shopProductSpecValueService.saveOrUpdate(shopProductSpecValue);
                }
            }
        }

        // 保存商品属性
        for(Integer key : productInfoRequest.getSpecs().keySet()) {
            // 查找数据库当前商品属性
            ShopProductSpecValue existingValue = shopProductSpecValueService.getOne(
                    new LambdaQueryWrapper<ShopProductSpecValue>()
                            .eq(ShopProductSpecValue::getProductId, shopProductInfo.getId())
                            .eq(ShopProductSpecValue::getSpecId, key)
            );
            if(existingValue != null) {
                // 更新
                existingValue.setValue(productInfoRequest.getSpecs().get(key));
                shopProductSpecValueService.updateById(existingValue);
            } else {
                // 创建
                ShopProductSpecValue shopProductSpecValue = new ShopProductSpecValue();
                shopProductSpecValue.setProductId(shopProductInfo.getId());
                shopProductSpecValue.setSpecId(key);
                shopProductSpecValue.setValue(productInfoRequest.getSpecs().get(key));
                shopProductSpecValueService.save(shopProductSpecValue);
            }
        }

        //查找要删除的sku
        List<ShopProductSku> shopProductSkuList = deleteCodes.isEmpty() ? new ArrayList<>() :shopProductSkuService
                .list(new LambdaQueryWrapper<ShopProductSku>().eq(ShopProductSku::getProductId, shopProductInfo.getId())
                        .in(ShopProductSku::getCode, deleteCodes)
                );
        // 获取要删除的sku的 List<Integer>
        List<Integer> deleteIds = shopProductSkuList.stream().map(ShopProductSku::getId).collect(Collectors.toList());
        if(!deleteIds.isEmpty()) {
            shopProductSkuService.removeByIds(deleteIds);
            shopProductSpecValueService.remove(new LambdaQueryWrapper<ShopProductSpecValue>().eq(ShopProductSpecValue::getProductId, shopProductInfo.getId())
                    .in(ShopProductSpecValue::getSkuId, deleteIds)
            );
        }

        // 保存商品货运信息
        if(productInfoRequest.getFeightInfo() != null) {
            // 检查是否有商品的货运信息
            ShopProductFreight shopProductFreight = shopProductFreightService.getOne(new LambdaQueryWrapper<ShopProductFreight>()
                    .eq(ShopProductFreight::getProductId, shopProductInfo.getId()).last("limit 1")
            );
            if(shopProductFreight != null) {
                // 存在则更新
                shopProductFreight.setTemplateId(productInfoRequest.getFeightInfo().getTemplateId());
                shopProductFreight.setWeight(productInfoRequest.getFeightInfo().getWeight());
                shopProductFreight.setLength(productInfoRequest.getFeightInfo().getLength());
                shopProductFreight.setWidth(productInfoRequest.getFeightInfo().getWidth());
                shopProductFreight.setHeight(productInfoRequest.getFeightInfo().getHeight());
                shopProductFreight.setVolumetricWeight(productInfoRequest.getFeightInfo().getVolumetricWeight());
                shopProductFreightService.updateById(shopProductFreight);
            } else {
                shopProductFreight = BeanUtils.copy(productInfoRequest.getFeightInfo(), ShopProductFreight.class);
                shopProductFreight.setProductId(shopProductInfo.getId());
                shopProductFreightService.save(shopProductFreight);
            }
        }

        DynamicDataSourceContextHolder.poll();
        return R.success();
    }

    /**
     * 新增商品信息
     */
    @PostMapping
    @Node(value = "shopProductInfo::add", name = "新增 商品信息")
    public R<Boolean> add(@RequestBody ShopProductInfo shopProductInfo) {
        return R.success(shopProductInfoService.save(shopProductInfo));
    }

    /**
     * 修改商品信息
     */
    @PutMapping
    @Node(value = "shopProductInfo::edit", name = "修改 商品信息")
    public R<Boolean> edit(@RequestBody ShopProductInfo shopProductInfo) {
        return R.success(shopProductInfoService.updateById(shopProductInfo));
    }

    /**
     * 删除商品信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductInfo::remove", name = "删除 商品信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该商品信息 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "shopProductInfo::options", name = "获取 商品信息 选项")
    public R<List<OptionVo<Long, String>>> options(ShopProductInfo shopProductInfo) {
        LambdaQueryWrapper<ShopProductInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shopProductInfo.getShopId() != null,ShopProductInfo::getShopId, shopProductInfo.getShopId());
        queryWrapper.like(StringUtils.isNotBlank(shopProductInfo.getName()), ShopProductInfo::getName, shopProductInfo.getName());
        queryWrapper.like(StringUtils.isNotBlank(shopProductInfo.getSpuCode()), ShopProductInfo::getSpuCode, shopProductInfo.getSpuCode());

        List<ShopProductInfo> list = shopProductInfoService.list(queryWrapper);
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(ShopProductInfo item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getSpuCode());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 商品信息 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductInfo.class)
    @Node(value = "shopProductInfo::exportExcel", name = "导出 商品信息 Excel")
    public Object exportExcel(ShopProductInfo shopProductInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductInfo> lqw = new LambdaQueryWrapper<ShopProductInfo>();
        lqw.eq(shopProductInfo.getId() != null, ShopProductInfo::getId ,shopProductInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSpuCode()),ShopProductInfo::getSpuCode ,shopProductInfo.getSpuCode());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getName()),ShopProductInfo::getName ,shopProductInfo.getName());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getSubTitle()),ShopProductInfo::getSubTitle ,shopProductInfo.getSubTitle());
//        lqw.like(StringUtils.isNotBlank(shopProductInfo.getQty()),ShopProductInfo::getQty ,shopProductInfo.getQty());
        lqw.eq(shopProductInfo.getOriginalPrice() != null, ShopProductInfo::getOriginalPrice ,shopProductInfo.getOriginalPrice());
        lqw.eq(shopProductInfo.getSellingPrice() != null, ShopProductInfo::getSellingPrice ,shopProductInfo.getSellingPrice());
        lqw.eq(shopProductInfo.getShopId() != null, ShopProductInfo::getShopId ,shopProductInfo.getShopId());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getMainImage()),ShopProductInfo::getMainImage ,shopProductInfo.getMainImage());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getThumbnails()),ShopProductInfo::getThumbnails ,shopProductInfo.getThumbnails());
        lqw.like(StringUtils.isNotBlank(shopProductInfo.getDetailHtml()),ShopProductInfo::getDetailHtml ,shopProductInfo.getDetailHtml());
        lqw.eq(shopProductInfo.getCommentCount() != null, ShopProductInfo::getCommentCount ,shopProductInfo.getCommentCount());
        lqw.eq(shopProductInfo.getCategoryId() != null,ShopProductInfo::getCategoryId ,shopProductInfo.getCategoryId());
        lqw.like(shopProductInfo.getShelfStatus() != null,ShopProductInfo::getShelfStatus ,shopProductInfo.getShelfStatus());
        lqw.eq(shopProductInfo.getAuditStatus() != null, ShopProductInfo::getAuditStatus ,shopProductInfo.getAuditStatus());
        lqw.eq(shopProductInfo.getUpdatedTime() != null, ShopProductInfo::getUpdatedTime ,shopProductInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductInfo::getId);
        return shopProductInfoService.list(lqw);
    }

    /**
     * 导入 商品信息 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductInfo::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductInfo.class, new ReadListener<ShopProductInfo>() {
                @Override
                public void invoke(ShopProductInfo shopProductInfo, AnalysisContext analysisContext) {
                    shopProductInfoService.save(shopProductInfo);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    //nothing todo
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(e.getMessage());
        }
    }
}