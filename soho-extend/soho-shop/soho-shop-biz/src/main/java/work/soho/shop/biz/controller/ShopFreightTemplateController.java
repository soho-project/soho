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
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.api.vo.FreightTemplateVo;
import work.soho.shop.biz.domain.ShopFreightRule;
import work.soho.shop.biz.domain.ShopFreightTemplate;
import work.soho.shop.biz.service.ShopFreightRuleService;
import work.soho.shop.biz.service.ShopFreightTemplateService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运费模板主表Controller
 *
 * @author fang
 */
@Api(tags = "运费模板主表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopFreightTemplate" )
public class ShopFreightTemplateController {

    private final ShopFreightTemplateService shopFreightTemplateService;
    private final ShopFreightRuleService shopFreightRuleService;
//    private final AdminDictApiService adminDictApiService;

    /**
     * 查询运费模板主表列表
     */
    @GetMapping("/list")
    @Node(value = "shopFreightTemplate::list", name = "获取 运费模板主表 列表")
    public R<PageSerializable<ShopFreightTemplate>> list(ShopFreightTemplate shopFreightTemplate, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopFreightTemplate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopFreightTemplate.getId() != null, ShopFreightTemplate::getId ,shopFreightTemplate.getId());
        lqw.eq(shopFreightTemplate.getShopId() != null, ShopFreightTemplate::getShopId ,shopFreightTemplate.getShopId());
        lqw.like(StringUtils.isNotBlank(shopFreightTemplate.getName()),ShopFreightTemplate::getName ,shopFreightTemplate.getName());
        lqw.eq(shopFreightTemplate.getType() != null, ShopFreightTemplate::getType ,shopFreightTemplate.getType());
        lqw.eq(shopFreightTemplate.getValuationMethod() != null, ShopFreightTemplate::getValuationMethod ,shopFreightTemplate.getValuationMethod());
        lqw.eq(shopFreightTemplate.getIsFreeShipping() != null, ShopFreightTemplate::getIsFreeShipping ,shopFreightTemplate.getIsFreeShipping());
        lqw.eq(shopFreightTemplate.getFreeConditionType() != null, ShopFreightTemplate::getFreeConditionType ,shopFreightTemplate.getFreeConditionType());
        lqw.eq(shopFreightTemplate.getFreeConditionValue() != null, ShopFreightTemplate::getFreeConditionValue ,shopFreightTemplate.getFreeConditionValue());
        lqw.eq(shopFreightTemplate.getIncludeSpecialRegions() != null, ShopFreightTemplate::getIncludeSpecialRegions ,shopFreightTemplate.getIncludeSpecialRegions());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopFreightTemplate::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopFreightTemplate::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopFreightTemplate.getUpdatedTime() != null, ShopFreightTemplate::getUpdatedTime ,shopFreightTemplate.getUpdatedTime());
        lqw.orderByDesc(ShopFreightTemplate::getId);
        List<ShopFreightTemplate> list = shopFreightTemplateService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取运费模板主表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopFreightTemplate::getInfo", name = "获取 运费模板主表 信息")
    public R<ShopFreightTemplate> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopFreightTemplateService.getById(id));
    }

    /**
     * 获取运费模板详细信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/details/{id}" )
    @Node(value = "shopFreightTemplate::getDetails", name = "获取 运费模板主表 详细信息")
    public R<FreightTemplateVo> getDetails(@PathVariable("id" ) Long id) {
        ShopFreightTemplate shopFreightTemplate = shopFreightTemplateService.getById(id);
        Assert.notNull(shopFreightTemplate, "运费模板不存在");
        FreightTemplateVo freightTemplateVo = BeanUtils.copy(shopFreightTemplate, FreightTemplateVo.class);
        freightTemplateVo.setRules(
                shopFreightRuleService
                        .list(new LambdaQueryWrapper<ShopFreightRule>().eq(ShopFreightRule::getTemplateId, id))
                        .stream().map(item->BeanUtils.copy(item, FreightTemplateVo.FreightTemplateRuleVo.class))
                .collect(Collectors.toList())
        );
        return R.success(freightTemplateVo);
    }

    /**
     * 新增运费模板主表
     */
    @PostMapping
    @Node(value = "shopFreightTemplate::add", name = "新增 运费模板主表")
    public R<Boolean> add(@RequestBody ShopFreightTemplate shopFreightTemplate) {
        return R.success(shopFreightTemplateService.save(shopFreightTemplate));
    }

    /**
     * 保存运费模板
     */
    @PostMapping("/details")
    @Node(value = "shopFreightTemplate::save", name = "保存 运费模板主表")
    public R<Boolean> save(@RequestBody FreightTemplateVo freightTemplateRequest) {
        try {
            DynamicDataSourceContextHolder.push("shop");
            ShopFreightTemplate shopFreightTemplate = BeanUtils.copy(freightTemplateRequest, ShopFreightTemplate.class);
            shopFreightTemplateService.saveOrUpdate(shopFreightTemplate);

            // 保存运费规则
            List<Long> ruleIds = freightTemplateRequest.getRules().stream().map(item->item.getId()).collect(Collectors.toList());
            // 从数据库删除本次未提交的运费规则
            shopFreightRuleService.remove(
                    new LambdaQueryWrapper<ShopFreightRule>().
                            eq(ShopFreightRule::getTemplateId, shopFreightTemplate.getId())
                            .notIn(ruleIds.size()>0, ShopFreightRule::getId, ruleIds));
            freightTemplateRequest.getRules().forEach(item->{
                // 保存运费规则
                ShopFreightRule shopFreightRule = BeanUtils.copy(item, ShopFreightRule.class);
                shopFreightRule.setTemplateId(shopFreightTemplate.getId());
                shopFreightRuleService.saveOrUpdate(shopFreightRule);
            });

            return R.success();
        } catch (Exception e) {
            return R.error(e.getMessage());
        } finally {
            DynamicDataSourceContextHolder.poll();
        }

    }

    /**
     * 修改运费模板主表
     */
    @PutMapping
    @Node(value = "shopFreightTemplate::edit", name = "修改 运费模板主表")
    public R<Boolean> edit(@RequestBody ShopFreightTemplate shopFreightTemplate) {
        return R.success(shopFreightTemplateService.updateById(shopFreightTemplate));
    }

    /**
     * 删除运费模板主表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopFreightTemplate::remove", name = "删除 运费模板主表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopFreightTemplateService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该运费模板主表 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "shopFreightTemplate::options", name = "获取 运费模板主表 选项")
    public R<List<OptionVo<Long, String>>> options(Long shopId) {
        LambdaQueryWrapper<ShopFreightTemplate> lqw = new LambdaQueryWrapper<ShopFreightTemplate>();
        lqw.eq(shopId != null, ShopFreightTemplate::getShopId ,shopId);
        List<ShopFreightTemplate> list = shopFreightTemplateService.list(lqw);
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(ShopFreightTemplate item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 运费模板主表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopFreightTemplate.class)
    @Node(value = "shopFreightTemplate::exportExcel", name = "导出 运费模板主表 Excel")
    public Object exportExcel(ShopFreightTemplate shopFreightTemplate, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopFreightTemplate> lqw = new LambdaQueryWrapper<ShopFreightTemplate>();
        lqw.eq(shopFreightTemplate.getId() != null, ShopFreightTemplate::getId ,shopFreightTemplate.getId());
        lqw.eq(shopFreightTemplate.getShopId() != null, ShopFreightTemplate::getShopId ,shopFreightTemplate.getShopId());
        lqw.like(StringUtils.isNotBlank(shopFreightTemplate.getName()),ShopFreightTemplate::getName ,shopFreightTemplate.getName());
        lqw.eq(shopFreightTemplate.getType() != null, ShopFreightTemplate::getType ,shopFreightTemplate.getType());
        lqw.eq(shopFreightTemplate.getValuationMethod() != null, ShopFreightTemplate::getValuationMethod ,shopFreightTemplate.getValuationMethod());
        lqw.eq(shopFreightTemplate.getIsFreeShipping() != null, ShopFreightTemplate::getIsFreeShipping ,shopFreightTemplate.getIsFreeShipping());
        lqw.eq(shopFreightTemplate.getFreeConditionType() != null, ShopFreightTemplate::getFreeConditionType ,shopFreightTemplate.getFreeConditionType());
        lqw.eq(shopFreightTemplate.getFreeConditionValue() != null, ShopFreightTemplate::getFreeConditionValue ,shopFreightTemplate.getFreeConditionValue());
        lqw.eq(shopFreightTemplate.getIncludeSpecialRegions() != null, ShopFreightTemplate::getIncludeSpecialRegions ,shopFreightTemplate.getIncludeSpecialRegions());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopFreightTemplate::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopFreightTemplate::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopFreightTemplate.getUpdatedTime() != null, ShopFreightTemplate::getUpdatedTime ,shopFreightTemplate.getUpdatedTime());
        lqw.orderByDesc(ShopFreightTemplate::getId);
        return shopFreightTemplateService.list(lqw);
    }

    /**
     * 导入 运费模板主表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopFreightTemplate::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopFreightTemplate.class, new ReadListener<ShopFreightTemplate>() {
                @Override
                public void invoke(ShopFreightTemplate shopFreightTemplate, AnalysisContext analysisContext) {
                    shopFreightTemplateService.save(shopFreightTemplate);
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