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
import work.soho.shop.biz.domain.ShopFreightRule;
import work.soho.shop.biz.service.ShopFreightRuleService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 运费详细规则表Controller
 *
 * @author fang
 */
@Api(tags = "运费详细规则表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopFreightRule" )
public class ShopFreightRuleController {

    private final ShopFreightRuleService shopFreightRuleService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询运费详细规则表列表
     */
    @GetMapping("/list")
    @Node(value = "shopFreightRule::list", name = "获取 运费详细规则表 列表")
    public R<PageSerializable<ShopFreightRule>> list(ShopFreightRule shopFreightRule, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopFreightRule> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopFreightRule.getId() != null, ShopFreightRule::getId ,shopFreightRule.getId());
        lqw.eq(shopFreightRule.getTemplateId() != null, ShopFreightRule::getTemplateId ,shopFreightRule.getTemplateId());
        lqw.like(StringUtils.isNotBlank(shopFreightRule.getRegionCodes()),ShopFreightRule::getRegionCodes ,shopFreightRule.getRegionCodes());
        lqw.eq(shopFreightRule.getFirstUnit() != null, ShopFreightRule::getFirstUnit ,shopFreightRule.getFirstUnit());
        lqw.eq(shopFreightRule.getFirstUnitPrice() != null, ShopFreightRule::getFirstUnitPrice ,shopFreightRule.getFirstUnitPrice());
        lqw.eq(shopFreightRule.getContinueUnit() != null, ShopFreightRule::getContinueUnit ,shopFreightRule.getContinueUnit());
        lqw.eq(shopFreightRule.getContinueUnitPrice() != null, ShopFreightRule::getContinueUnitPrice ,shopFreightRule.getContinueUnitPrice());
        lqw.eq(shopFreightRule.getIsDefault() != null, ShopFreightRule::getIsDefault ,shopFreightRule.getIsDefault());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopFreightRule::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopFreightRule::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopFreightRule::getId);
        List<ShopFreightRule> list = shopFreightRuleService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取运费详细规则表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopFreightRule::getInfo", name = "获取 运费详细规则表 详细信息")
    public R<ShopFreightRule> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopFreightRuleService.getById(id));
    }

    /**
     * 新增运费详细规则表
     */
    @PostMapping
    @Node(value = "shopFreightRule::add", name = "新增 运费详细规则表")
    public R<Boolean> add(@RequestBody ShopFreightRule shopFreightRule) {
        return R.success(shopFreightRuleService.save(shopFreightRule));
    }

    /**
     * 修改运费详细规则表
     */
    @PutMapping
    @Node(value = "shopFreightRule::edit", name = "修改 运费详细规则表")
    public R<Boolean> edit(@RequestBody ShopFreightRule shopFreightRule) {
        return R.success(shopFreightRuleService.updateById(shopFreightRule));
    }

    /**
     * 删除运费详细规则表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopFreightRule::remove", name = "删除 运费详细规则表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopFreightRuleService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 运费详细规则表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopFreightRule.class)
    @Node(value = "shopFreightRule::exportExcel", name = "导出 运费详细规则表 Excel")
    public Object exportExcel(ShopFreightRule shopFreightRule, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopFreightRule> lqw = new LambdaQueryWrapper<ShopFreightRule>();
        lqw.eq(shopFreightRule.getId() != null, ShopFreightRule::getId ,shopFreightRule.getId());
        lqw.eq(shopFreightRule.getTemplateId() != null, ShopFreightRule::getTemplateId ,shopFreightRule.getTemplateId());
        lqw.like(StringUtils.isNotBlank(shopFreightRule.getRegionCodes()),ShopFreightRule::getRegionCodes ,shopFreightRule.getRegionCodes());
        lqw.eq(shopFreightRule.getFirstUnit() != null, ShopFreightRule::getFirstUnit ,shopFreightRule.getFirstUnit());
        lqw.eq(shopFreightRule.getFirstUnitPrice() != null, ShopFreightRule::getFirstUnitPrice ,shopFreightRule.getFirstUnitPrice());
        lqw.eq(shopFreightRule.getContinueUnit() != null, ShopFreightRule::getContinueUnit ,shopFreightRule.getContinueUnit());
        lqw.eq(shopFreightRule.getContinueUnitPrice() != null, ShopFreightRule::getContinueUnitPrice ,shopFreightRule.getContinueUnitPrice());
        lqw.eq(shopFreightRule.getIsDefault() != null, ShopFreightRule::getIsDefault ,shopFreightRule.getIsDefault());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopFreightRule::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopFreightRule::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopFreightRule::getId);
        return shopFreightRuleService.list(lqw);
    }

    /**
     * 导入 运费详细规则表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopFreightRule::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopFreightRule.class, new ReadListener<ShopFreightRule>() {
                @Override
                public void invoke(ShopFreightRule shopFreightRule, AnalysisContext analysisContext) {
                    shopFreightRuleService.save(shopFreightRule);
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