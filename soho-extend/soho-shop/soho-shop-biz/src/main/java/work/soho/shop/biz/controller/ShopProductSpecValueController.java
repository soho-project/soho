package work.soho.shop.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopProductSpecValue;
import work.soho.shop.biz.service.ShopProductSpecValueService;

import java.util.Arrays;
import java.util.List;
/**
 * 商品规格值Controller
 *
 * @author fang
 */
@Api(tags = "商品规格值")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductSpecValue" )
public class ShopProductSpecValueController {

    private final ShopProductSpecValueService shopProductSpecValueService;

    /**
     * 查询商品规格值列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductSpecValue::list", name = "获取 商品规格值 列表")
    public R<PageSerializable<ShopProductSpecValue>> list(ShopProductSpecValue shopProductSpecValue, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductSpecValue> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductSpecValue.getId() != null, ShopProductSpecValue::getId ,shopProductSpecValue.getId());
        lqw.eq(shopProductSpecValue.getProductId() != null, ShopProductSpecValue::getProductId ,shopProductSpecValue.getProductId());
        lqw.eq(shopProductSpecValue.getSkuId() != null, ShopProductSpecValue::getSkuId ,shopProductSpecValue.getSkuId());
        lqw.eq(shopProductSpecValue.getSpecId() != null, ShopProductSpecValue::getSpecId ,shopProductSpecValue.getSpecId());
        //lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getSortOrder()),ShopProductSpecValue::getSortOrder ,shopProductSpecValue.getSortOrder());
        lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getValue()),ShopProductSpecValue::getValue ,shopProductSpecValue.getValue());
        lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getExtend()),ShopProductSpecValue::getExtend ,shopProductSpecValue.getExtend());
        lqw.eq(shopProductSpecValue.getUpdatedTime() != null, ShopProductSpecValue::getUpdatedTime ,shopProductSpecValue.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductSpecValue::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductSpecValue::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductSpecValue::getId);
        List<ShopProductSpecValue> list = shopProductSpecValueService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品规格值详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductSpecValue::getInfo", name = "获取 商品规格值 详细信息")
    public R<ShopProductSpecValue> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductSpecValueService.getById(id));
    }

    /**
     * 新增商品规格值
     */
    @PostMapping
    @Node(value = "shopProductSpecValue::add", name = "新增 商品规格值")
    public R<Boolean> add(@RequestBody ShopProductSpecValue shopProductSpecValue) {
        return R.success(shopProductSpecValueService.save(shopProductSpecValue));
    }

    /**
     * 修改商品规格值
     */
    @PutMapping
    @Node(value = "shopProductSpecValue::edit", name = "修改 商品规格值")
    public R<Boolean> edit(@RequestBody ShopProductSpecValue shopProductSpecValue) {
        return R.success(shopProductSpecValueService.updateById(shopProductSpecValue));
    }

    /**
     * 删除商品规格值
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductSpecValue::remove", name = "删除 商品规格值")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductSpecValueService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 商品规格值 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductSpecValue.class)
    @Node(value = "shopProductSpecValue::exportExcel", name = "导出 商品规格值 Excel")
    public Object exportExcel(ShopProductSpecValue shopProductSpecValue, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductSpecValue> lqw = new LambdaQueryWrapper<ShopProductSpecValue>();
        lqw.eq(shopProductSpecValue.getId() != null, ShopProductSpecValue::getId ,shopProductSpecValue.getId());
        lqw.eq(shopProductSpecValue.getProductId() != null, ShopProductSpecValue::getProductId ,shopProductSpecValue.getProductId());
        lqw.eq(shopProductSpecValue.getSkuId() != null, ShopProductSpecValue::getSkuId ,shopProductSpecValue.getSkuId());
        lqw.eq(shopProductSpecValue.getSpecId() != null, ShopProductSpecValue::getSpecId ,shopProductSpecValue.getSpecId());
        //lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getSortOrder()),ShopProductSpecValue::getSortOrder ,shopProductSpecValue.getSortOrder());
        lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getValue()),ShopProductSpecValue::getValue ,shopProductSpecValue.getValue());
        lqw.like(StringUtils.isNotBlank(shopProductSpecValue.getExtend()),ShopProductSpecValue::getExtend ,shopProductSpecValue.getExtend());
        lqw.eq(shopProductSpecValue.getUpdatedTime() != null, ShopProductSpecValue::getUpdatedTime ,shopProductSpecValue.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductSpecValue::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductSpecValue::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductSpecValue::getId);
        return shopProductSpecValueService.list(lqw);
    }

    /**
     * 导入 商品规格值 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductSpecValue::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductSpecValue.class, new ReadListener<ShopProductSpecValue>() {
                @Override
                public void invoke(ShopProductSpecValue shopProductSpecValue, AnalysisContext analysisContext) {
                    shopProductSpecValueService.save(shopProductSpecValue);
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