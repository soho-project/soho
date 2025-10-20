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
import work.soho.shop.biz.domain.ShopProductSku;
import work.soho.shop.biz.service.ShopProductSkuService;

import java.util.Arrays;
import java.util.List;
/**
 * Controller
 *
 * @author fang
 */
@Api(tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductSku" )
public class ShopProductSkuController {

    private final ShopProductSkuService shopProductSkuService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductSku::list", name = "获取  列表")
    public R<PageSerializable<ShopProductSku>> list(ShopProductSku shopProductSku, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductSku> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductSku.getId() != null, ShopProductSku::getId ,shopProductSku.getId());
        lqw.like(shopProductSku.getProductId() != null,ShopProductSku::getProductId ,shopProductSku.getProductId());
        lqw.like(StringUtils.isNotBlank(shopProductSku.getCode()),ShopProductSku::getCode ,shopProductSku.getCode());
        lqw.eq(shopProductSku.getQty() != null, ShopProductSku::getQty ,shopProductSku.getQty());
        lqw.eq(shopProductSku.getOriginalPrice() != null, ShopProductSku::getOriginalPrice ,shopProductSku.getOriginalPrice());
        lqw.eq(shopProductSku.getSellingPrice() != null, ShopProductSku::getSellingPrice ,shopProductSku.getSellingPrice());
        lqw.like(StringUtils.isNotBlank(shopProductSku.getMainImage()),ShopProductSku::getMainImage ,shopProductSku.getMainImage());
        lqw.eq(shopProductSku.getUpdatedTime() != null, ShopProductSku::getUpdatedTime ,shopProductSku.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductSku::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductSku::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductSku::getId);
        List<ShopProductSku> list = shopProductSkuService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductSku::getInfo", name = "获取  详细信息")
    public R<ShopProductSku> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductSkuService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "shopProductSku::add", name = "新增 ")
    public R<Boolean> add(@RequestBody ShopProductSku shopProductSku) {
        return R.success(shopProductSkuService.save(shopProductSku));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "shopProductSku::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody ShopProductSku shopProductSku) {
        return R.success(shopProductSkuService.updateById(shopProductSku));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductSku::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductSkuService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductSku.class)
    @Node(value = "shopProductSku::exportExcel", name = "导出  Excel")
    public Object exportExcel(ShopProductSku shopProductSku, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductSku> lqw = new LambdaQueryWrapper<ShopProductSku>();
        lqw.eq(shopProductSku.getId() != null, ShopProductSku::getId ,shopProductSku.getId());
        lqw.like(shopProductSku.getProductId() != null,ShopProductSku::getProductId ,shopProductSku.getProductId());
        lqw.like(StringUtils.isNotBlank(shopProductSku.getCode()),ShopProductSku::getCode ,shopProductSku.getCode());
        lqw.eq(shopProductSku.getQty() != null, ShopProductSku::getQty ,shopProductSku.getQty());
        lqw.eq(shopProductSku.getOriginalPrice() != null, ShopProductSku::getOriginalPrice ,shopProductSku.getOriginalPrice());
        lqw.eq(shopProductSku.getSellingPrice() != null, ShopProductSku::getSellingPrice ,shopProductSku.getSellingPrice());
        lqw.like(StringUtils.isNotBlank(shopProductSku.getMainImage()),ShopProductSku::getMainImage ,shopProductSku.getMainImage());
        lqw.eq(shopProductSku.getUpdatedTime() != null, ShopProductSku::getUpdatedTime ,shopProductSku.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductSku::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductSku::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopProductSku::getId);
        return shopProductSkuService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductSku::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductSku.class, new ReadListener<ShopProductSku>() {
                @Override
                public void invoke(ShopProductSku shopProductSku, AnalysisContext analysisContext) {
                    shopProductSkuService.save(shopProductSku);
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