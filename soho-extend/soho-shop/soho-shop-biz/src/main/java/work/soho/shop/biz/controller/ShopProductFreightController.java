package work.soho.shop.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.shop.biz.domain.ShopProductFreight;
import work.soho.shop.biz.service.ShopProductFreightService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 商品与运费模板关联表Controller
 *
 * @author fang
 */
@Api(tags = "商品与运费模板关联表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductFreight" )
public class ShopProductFreightController {

    private final ShopProductFreightService shopProductFreightService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询商品与运费模板关联表列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductFreight::list", name = "获取 商品与运费模板关联表 列表")
    public R<PageSerializable<ShopProductFreight>> list(ShopProductFreight shopProductFreight, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductFreight> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductFreight.getId() != null, ShopProductFreight::getId ,shopProductFreight.getId());
        lqw.eq(shopProductFreight.getProductId() != null, ShopProductFreight::getProductId ,shopProductFreight.getProductId());
        lqw.eq(shopProductFreight.getTemplateId() != null, ShopProductFreight::getTemplateId ,shopProductFreight.getTemplateId());
        lqw.eq(shopProductFreight.getWeight() != null, ShopProductFreight::getWeight ,shopProductFreight.getWeight());
        lqw.eq(shopProductFreight.getLength() != null, ShopProductFreight::getLength ,shopProductFreight.getLength());
        lqw.eq(shopProductFreight.getWidth() != null, ShopProductFreight::getWidth ,shopProductFreight.getWidth());
        lqw.eq(shopProductFreight.getHeight() != null, ShopProductFreight::getHeight ,shopProductFreight.getHeight());
        lqw.eq(shopProductFreight.getVolumetricWeight() != null, ShopProductFreight::getVolumetricWeight ,shopProductFreight.getVolumetricWeight());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductFreight::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductFreight::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopProductFreight.getUpdatedTime() != null, ShopProductFreight::getUpdatedTime ,shopProductFreight.getUpdatedTime());
        lqw.orderByDesc(ShopProductFreight::getId);
        List<ShopProductFreight> list = shopProductFreightService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品与运费模板关联表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductFreight::getInfo", name = "获取 商品与运费模板关联表 详细信息")
    public R<ShopProductFreight> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductFreightService.getById(id));
    }

    /**
     * 新增商品与运费模板关联表
     */
    @PostMapping
    @Node(value = "shopProductFreight::add", name = "新增 商品与运费模板关联表")
    public R<Boolean> add(@RequestBody ShopProductFreight shopProductFreight) {
        return R.success(shopProductFreightService.save(shopProductFreight));
    }

    /**
     * 修改商品与运费模板关联表
     */
    @PutMapping
    @Node(value = "shopProductFreight::edit", name = "修改 商品与运费模板关联表")
    public R<Boolean> edit(@RequestBody ShopProductFreight shopProductFreight) {
        return R.success(shopProductFreightService.updateById(shopProductFreight));
    }

    /**
     * 删除商品与运费模板关联表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductFreight::remove", name = "删除 商品与运费模板关联表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductFreightService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 商品与运费模板关联表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductFreight.class)
    @Node(value = "shopProductFreight::exportExcel", name = "导出 商品与运费模板关联表 Excel")
    public Object exportExcel(ShopProductFreight shopProductFreight, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductFreight> lqw = new LambdaQueryWrapper<ShopProductFreight>();
        lqw.eq(shopProductFreight.getId() != null, ShopProductFreight::getId ,shopProductFreight.getId());
        lqw.eq(shopProductFreight.getProductId() != null, ShopProductFreight::getProductId ,shopProductFreight.getProductId());
        lqw.eq(shopProductFreight.getTemplateId() != null, ShopProductFreight::getTemplateId ,shopProductFreight.getTemplateId());
        lqw.eq(shopProductFreight.getWeight() != null, ShopProductFreight::getWeight ,shopProductFreight.getWeight());
        lqw.eq(shopProductFreight.getLength() != null, ShopProductFreight::getLength ,shopProductFreight.getLength());
        lqw.eq(shopProductFreight.getWidth() != null, ShopProductFreight::getWidth ,shopProductFreight.getWidth());
        lqw.eq(shopProductFreight.getHeight() != null, ShopProductFreight::getHeight ,shopProductFreight.getHeight());
        lqw.eq(shopProductFreight.getVolumetricWeight() != null, ShopProductFreight::getVolumetricWeight ,shopProductFreight.getVolumetricWeight());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductFreight::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductFreight::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopProductFreight.getUpdatedTime() != null, ShopProductFreight::getUpdatedTime ,shopProductFreight.getUpdatedTime());
        lqw.orderByDesc(ShopProductFreight::getId);
        return shopProductFreightService.list(lqw);
    }

    /**
     * 导入 商品与运费模板关联表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductFreight::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductFreight.class, new ReadListener<ShopProductFreight>() {
                @Override
                public void invoke(ShopProductFreight shopProductFreight, AnalysisContext analysisContext) {
                    shopProductFreightService.save(shopProductFreight);
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