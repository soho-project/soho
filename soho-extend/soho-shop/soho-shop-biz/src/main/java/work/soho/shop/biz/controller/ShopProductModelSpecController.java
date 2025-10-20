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
import work.soho.shop.biz.domain.ShopProductModelSpec;
import work.soho.shop.biz.service.ShopProductModelSpecService;

import java.util.Arrays;
import java.util.List;
/**
 * 模型属性信息Controller
 *
 * @author fang
 */
@Api(tags = "模型属性信息")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductModelSpec" )
public class ShopProductModelSpecController {

    private final ShopProductModelSpecService shopProductModelSpecService;

    /**
     * 查询模型属性信息列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductModelSpec::list", name = "获取 模型属性信息 列表")
    public R<PageSerializable<ShopProductModelSpec>> list(ShopProductModelSpec shopProductModelSpec, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductModelSpec> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductModelSpec.getId() != null, ShopProductModelSpec::getId ,shopProductModelSpec.getId());
        lqw.eq(shopProductModelSpec.getModelId() != null, ShopProductModelSpec::getModelId ,shopProductModelSpec.getModelId());
        lqw.like(StringUtils.isNotBlank(shopProductModelSpec.getName()),ShopProductModelSpec::getName ,shopProductModelSpec.getName());
        lqw.eq(shopProductModelSpec.getSortOrder() != null, ShopProductModelSpec::getSortOrder ,shopProductModelSpec.getSortOrder());
        lqw.eq(shopProductModelSpec.getType() != null, ShopProductModelSpec::getType ,shopProductModelSpec.getType());
        lqw.like(StringUtils.isNotBlank(shopProductModelSpec.getExtend()),ShopProductModelSpec::getExtend ,shopProductModelSpec.getExtend());
        lqw.eq(shopProductModelSpec.getUpdatedTime() != null, ShopProductModelSpec::getUpdatedTime ,shopProductModelSpec.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductModelSpec::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductModelSpec::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductModelSpec::getId);
        List<ShopProductModelSpec> list = shopProductModelSpecService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取模型属性信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductModelSpec::getInfo", name = "获取 模型属性信息 详细信息")
    public R<ShopProductModelSpec> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductModelSpecService.getById(id));
    }

    /**
     * 新增模型属性信息
     */
    @PostMapping
    @Node(value = "shopProductModelSpec::add", name = "新增 模型属性信息")
    public R<Boolean> add(@RequestBody ShopProductModelSpec shopProductModelSpec) {
        return R.success(shopProductModelSpecService.save(shopProductModelSpec));
    }

    /**
     * 修改模型属性信息
     */
    @PutMapping
    @Node(value = "shopProductModelSpec::edit", name = "修改 模型属性信息")
    public R<Boolean> edit(@RequestBody ShopProductModelSpec shopProductModelSpec) {
        return R.success(shopProductModelSpecService.updateById(shopProductModelSpec));
    }

    /**
     * 删除模型属性信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductModelSpec::remove", name = "删除 模型属性信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductModelSpecService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 模型属性信息 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductModelSpec.class)
    @Node(value = "shopProductModelSpec::exportExcel", name = "导出 模型属性信息 Excel")
    public Object exportExcel(ShopProductModelSpec shopProductModelSpec, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductModelSpec> lqw = new LambdaQueryWrapper<ShopProductModelSpec>();
        lqw.eq(shopProductModelSpec.getId() != null, ShopProductModelSpec::getId ,shopProductModelSpec.getId());
        lqw.eq(shopProductModelSpec.getModelId() != null, ShopProductModelSpec::getModelId ,shopProductModelSpec.getModelId());
        lqw.like(StringUtils.isNotBlank(shopProductModelSpec.getName()),ShopProductModelSpec::getName ,shopProductModelSpec.getName());
        lqw.eq(shopProductModelSpec.getSortOrder() != null, ShopProductModelSpec::getSortOrder ,shopProductModelSpec.getSortOrder());
        lqw.eq(shopProductModelSpec.getType() != null, ShopProductModelSpec::getType ,shopProductModelSpec.getType());
        lqw.like(StringUtils.isNotBlank(shopProductModelSpec.getExtend()),ShopProductModelSpec::getExtend ,shopProductModelSpec.getExtend());
        lqw.eq(shopProductModelSpec.getUpdatedTime() != null, ShopProductModelSpec::getUpdatedTime ,shopProductModelSpec.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductModelSpec::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductModelSpec::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductModelSpec::getId);
        return shopProductModelSpecService.list(lqw);
    }

    /**
     * 导入 模型属性信息 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductModelSpec::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductModelSpec.class, new ReadListener<ShopProductModelSpec>() {
                @Override
                public void invoke(ShopProductModelSpec shopProductModelSpec, AnalysisContext analysisContext) {
                    shopProductModelSpecService.save(shopProductModelSpec);
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