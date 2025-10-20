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
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.api.request.ModelSaveRequest;
import work.soho.shop.api.response.ModelResponse;
import work.soho.shop.api.vo.ModelSpecExtendVo;
import work.soho.shop.api.vo.ModelSpecVo;
import work.soho.shop.biz.domain.ShopProductCategory;
import work.soho.shop.biz.domain.ShopProductModel;
import work.soho.shop.biz.domain.ShopProductModelSpec;
import work.soho.shop.biz.service.ShopProductCategoryService;
import work.soho.shop.biz.service.ShopProductModelService;
import work.soho.shop.biz.service.ShopProductModelSpecService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品模型Controller
 *
 * @author fang
 */
@Api(tags = "商品模型")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductModel" )
public class ShopProductModelController {

    private final ShopProductModelService shopProductModelService;
    private final ShopProductModelSpecService shopProductModelSpecService;
    private final ShopProductCategoryService shopProductCategoryService;

    /**
     * 查询商品模型列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductModel::list", name = "获取 商品模型 列表")
    public R<PageSerializable<ShopProductModel>> list(ShopProductModel shopProductModel, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductModel> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductModel.getId() != null, ShopProductModel::getId ,shopProductModel.getId());
        lqw.like(StringUtils.isNotBlank(shopProductModel.getName()),ShopProductModel::getName ,shopProductModel.getName());
        lqw.eq(shopProductModel.getUpdatedTime() != null, ShopProductModel::getUpdatedTime ,shopProductModel.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductModel::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductModel::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductModel::getId);
        List<ShopProductModel> list = shopProductModelService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品模型详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductModel::getInfo", name = "获取 商品模型 详细信息")
    public R<ShopProductModel> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductModelService.getById(id));
    }

    /**
     * 模型详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/details/{id}" )
    @Node(value = "shopProductModel::getInfo", name = "获取 商品模型 详细信息")
    public R<ModelResponse> getDetails(@PathVariable("id" ) Long id) {
        ShopProductModel shopProductModel = shopProductModelService.getById(id);
        Assert.notNull(shopProductModel, "商品模型不存在");
        ModelResponse modelResponse = BeanUtils.copy(shopProductModel, ModelResponse.class);
        // 获取模型规格
        List<ShopProductModelSpec> specsList = shopProductModelSpecService.list(new LambdaQueryWrapper<ShopProductModelSpec>().eq(ShopProductModelSpec::getModelId, id).orderByDesc(ShopProductModelSpec::getSortOrder));
        List<ModelSpecVo> specVos = specsList.stream().map(spec -> {
            ModelSpecVo modelSpecVo = BeanUtils.copy(spec, ModelSpecVo.class);
            // 解析选项信息
            if(StringUtils.isNotEmpty(spec.getExtend())) {
                try {
                    ModelSpecExtendVo modelSpecExtendVo = JacksonUtils.toBean(spec.getExtend(), ModelSpecExtendVo.class);
                    modelSpecVo.setOptions(modelSpecExtendVo.getOptions());
                } catch (Exception e) {
                    log.error("解析选项信息失败：{}", spec.getExtend());
                    e.printStackTrace();
                }

            }
            return modelSpecVo;
        }).collect(Collectors.toList());

        modelResponse.setSpecs(specVos);

        return R.success(modelResponse);
    }

    /**
     * 模型详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/detailsByCategoryId/{id}" )
    @Node(value = "shopProductModel::detailsByCategoryId", name = "获取 商品模型 详细信息")
    public R<ModelResponse> getDetailsByCategoryId(@PathVariable("id" ) Long id) {
        ShopProductCategory shopProductCategory = shopProductCategoryService.getById(id);
        Assert.notNull(shopProductCategory, "商品模型不存在");
        return getDetails(Long.valueOf(shopProductCategory.getModelId()));
    }


    /**
     * 新增/编辑商品模型
     */
    @PostMapping("/save")
    @Node(value = "shopProductModel::saveModel", name = "新增 商品模型")
    public R<Boolean> saveModel(@RequestBody ModelSaveRequest modelSaveRequest) {
        DynamicDataSourceContextHolder.push("shop");
        //检查是模型新增还是修改
        ShopProductModel shopProductModel = null;
        if(modelSaveRequest.getId() == null) {
            // 新增
            shopProductModel = new ShopProductModel();
            shopProductModel.setName(modelSaveRequest.getName());
            shopProductModelService.save(shopProductModel);
        } else {
            //更新
            shopProductModel = shopProductModelService.getById(modelSaveRequest.getId());
            Assert.notNull(shopProductModel, "商品模型不存在");
            shopProductModel.setName(modelSaveRequest.getName());
            shopProductModelService.updateById(shopProductModel);
        }

        // 清理模型属性信息
        shopProductModelSpecService.remove(new LambdaQueryWrapper<ShopProductModelSpec>().eq(ShopProductModelSpec::getModelId, shopProductModel.getId()));

        ShopProductModel finalShopProductModel = shopProductModel;
        modelSaveRequest.getSpecs().forEach(spec -> {
            // 检查属性原来是否存在
            ShopProductModelSpec shopProductModelSpec = shopProductModelSpecService.getOne(
                    new LambdaQueryWrapper<ShopProductModelSpec>().eq(ShopProductModelSpec::getModelId, finalShopProductModel.getId()).eq(ShopProductModelSpec::getName, spec.getName()));

            if(shopProductModelSpec == null) {
                shopProductModelSpec = new ShopProductModelSpec();
                shopProductModelSpec.setModelId(finalShopProductModel.getId());
            }

            // 填充新值
            shopProductModelSpec.setName(spec.getName());
            shopProductModelSpec.setSortOrder(spec.getSortOrder());
            shopProductModelSpec.setType(spec.getType());
            shopProductModelSpec.setIsSale(spec.getIsSale());
            // 处理选项信息
            if(spec.getOptions() != null) {
                HashMap<String, List<String>> options = new HashMap<>();
                options.put("options", spec.getOptions());
                shopProductModelSpec.setExtend(JacksonUtils.toJson(options));
            }
            shopProductModelSpecService.saveOrUpdate(shopProductModelSpec);
        });
        DynamicDataSourceContextHolder.poll();
        return R.success();
    }

    /**
     * 新增商品模型
     */
    @PostMapping
    @Node(value = "shopProductModel::add", name = "新增 商品模型")
    public R<Boolean> add(@RequestBody ShopProductModel shopProductModel) {
        return R.success(shopProductModelService.save(shopProductModel));
    }

    /**
     * 修改商品模型
     */
    @PutMapping
    @Node(value = "shopProductModel::edit", name = "修改 商品模型")
    public R<Boolean> edit(@RequestBody ShopProductModel shopProductModel) {
        return R.success(shopProductModelService.updateById(shopProductModel));
    }

    /**
     * 删除商品模型
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductModel::remove", name = "删除 商品模型")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductModelService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该商品模型 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "shopProductModel::options", name = "获取 商品模型 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<ShopProductModel> list = shopProductModelService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(ShopProductModel item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 商品模型 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductModel.class)
    @Node(value = "shopProductModel::exportExcel", name = "导出 商品模型 Excel")
    public Object exportExcel(ShopProductModel shopProductModel, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductModel> lqw = new LambdaQueryWrapper<ShopProductModel>();
        lqw.eq(shopProductModel.getId() != null, ShopProductModel::getId ,shopProductModel.getId());
        lqw.like(StringUtils.isNotBlank(shopProductModel.getName()),ShopProductModel::getName ,shopProductModel.getName());
        lqw.eq(shopProductModel.getUpdatedTime() != null, ShopProductModel::getUpdatedTime ,shopProductModel.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductModel::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductModel::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductModel::getId);
        return shopProductModelService.list(lqw);
    }

    /**
     * 导入 商品模型 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductModel::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductModel.class, new ReadListener<ShopProductModel>() {
                @Override
                public void invoke(ShopProductModel shopProductModel, AnalysisContext analysisContext) {
                    shopProductModelService.save(shopProductModel);
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