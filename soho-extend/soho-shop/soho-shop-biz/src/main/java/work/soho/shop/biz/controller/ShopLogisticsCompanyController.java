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
import work.soho.shop.biz.domain.ShopLogisticsCompany;
import work.soho.shop.biz.service.ShopLogisticsCompanyService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 物流公司信息表Controller
 *
 * @author fang
 */
@Api(tags = "物流公司信息表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopLogisticsCompany" )
public class ShopLogisticsCompanyController {

    private final ShopLogisticsCompanyService shopLogisticsCompanyService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询物流公司信息表列表
     */
    @GetMapping("/list")
    @Node(value = "shopLogisticsCompany::list", name = "获取 物流公司信息表 列表")
    public R<PageSerializable<ShopLogisticsCompany>> list(ShopLogisticsCompany shopLogisticsCompany, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopLogisticsCompany> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopLogisticsCompany.getId() != null, ShopLogisticsCompany::getId ,shopLogisticsCompany.getId());
        lqw.like(StringUtils.isNotBlank(shopLogisticsCompany.getCompanyCode()),ShopLogisticsCompany::getCompanyCode ,shopLogisticsCompany.getCompanyCode());
        lqw.like(StringUtils.isNotBlank(shopLogisticsCompany.getCompanyName()),ShopLogisticsCompany::getCompanyName ,shopLogisticsCompany.getCompanyName());
        lqw.like(StringUtils.isNotBlank(shopLogisticsCompany.getContactPhone()),ShopLogisticsCompany::getContactPhone ,shopLogisticsCompany.getContactPhone());
        lqw.eq(shopLogisticsCompany.getIsEnabled() != null, ShopLogisticsCompany::getIsEnabled ,shopLogisticsCompany.getIsEnabled());
        lqw.eq(shopLogisticsCompany.getVolumetricRatio() != null, ShopLogisticsCompany::getVolumetricRatio ,shopLogisticsCompany.getVolumetricRatio());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopLogisticsCompany::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopLogisticsCompany::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopLogisticsCompany::getId);
        List<ShopLogisticsCompany> list = shopLogisticsCompanyService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取物流公司信息表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopLogisticsCompany::getInfo", name = "获取 物流公司信息表 详细信息")
    public R<ShopLogisticsCompany> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopLogisticsCompanyService.getById(id));
    }

    /**
     * 新增物流公司信息表
     */
    @PostMapping
    @Node(value = "shopLogisticsCompany::add", name = "新增 物流公司信息表")
    public R<Boolean> add(@RequestBody ShopLogisticsCompany shopLogisticsCompany) {
        return R.success(shopLogisticsCompanyService.save(shopLogisticsCompany));
    }

    /**
     * 修改物流公司信息表
     */
    @PutMapping
    @Node(value = "shopLogisticsCompany::edit", name = "修改 物流公司信息表")
    public R<Boolean> edit(@RequestBody ShopLogisticsCompany shopLogisticsCompany) {
        return R.success(shopLogisticsCompanyService.updateById(shopLogisticsCompany));
    }

    /**
     * 删除物流公司信息表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopLogisticsCompany::remove", name = "删除 物流公司信息表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopLogisticsCompanyService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 物流公司信息表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopLogisticsCompany.class)
    @Node(value = "shopLogisticsCompany::exportExcel", name = "导出 物流公司信息表 Excel")
    public Object exportExcel(ShopLogisticsCompany shopLogisticsCompany, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopLogisticsCompany> lqw = new LambdaQueryWrapper<ShopLogisticsCompany>();
        lqw.eq(shopLogisticsCompany.getId() != null, ShopLogisticsCompany::getId ,shopLogisticsCompany.getId());
        lqw.like(StringUtils.isNotBlank(shopLogisticsCompany.getCompanyCode()),ShopLogisticsCompany::getCompanyCode ,shopLogisticsCompany.getCompanyCode());
        lqw.like(StringUtils.isNotBlank(shopLogisticsCompany.getCompanyName()),ShopLogisticsCompany::getCompanyName ,shopLogisticsCompany.getCompanyName());
        lqw.like(StringUtils.isNotBlank(shopLogisticsCompany.getContactPhone()),ShopLogisticsCompany::getContactPhone ,shopLogisticsCompany.getContactPhone());
        lqw.eq(shopLogisticsCompany.getIsEnabled() != null, ShopLogisticsCompany::getIsEnabled ,shopLogisticsCompany.getIsEnabled());
        lqw.eq(shopLogisticsCompany.getVolumetricRatio() != null, ShopLogisticsCompany::getVolumetricRatio ,shopLogisticsCompany.getVolumetricRatio());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopLogisticsCompany::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopLogisticsCompany::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopLogisticsCompany::getId);
        return shopLogisticsCompanyService.list(lqw);
    }

    /**
     * 导入 物流公司信息表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopLogisticsCompany::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopLogisticsCompany.class, new ReadListener<ShopLogisticsCompany>() {
                @Override
                public void invoke(ShopLogisticsCompany shopLogisticsCompany, AnalysisContext analysisContext) {
                    shopLogisticsCompanyService.save(shopLogisticsCompany);
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