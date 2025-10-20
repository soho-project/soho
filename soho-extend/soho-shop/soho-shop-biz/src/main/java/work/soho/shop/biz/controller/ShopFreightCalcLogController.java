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
import work.soho.shop.biz.domain.ShopFreightCalcLog;
import work.soho.shop.biz.service.ShopFreightCalcLogService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 运费计算日志表Controller
 *
 * @author fang
 */
@Api(tags = "运费计算日志表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopFreightCalcLog" )
public class ShopFreightCalcLogController {

    private final ShopFreightCalcLogService shopFreightCalcLogService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询运费计算日志表列表
     */
    @GetMapping("/list")
    @Node(value = "shopFreightCalcLog::list", name = "获取 运费计算日志表 列表")
    public R<PageSerializable<ShopFreightCalcLog>> list(ShopFreightCalcLog shopFreightCalcLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopFreightCalcLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopFreightCalcLog.getId() != null, ShopFreightCalcLog::getId ,shopFreightCalcLog.getId());
        lqw.eq(shopFreightCalcLog.getOrderId() != null, ShopFreightCalcLog::getOrderId ,shopFreightCalcLog.getOrderId());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getSessionId()),ShopFreightCalcLog::getSessionId ,shopFreightCalcLog.getSessionId());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getRegionCode()),ShopFreightCalcLog::getRegionCode ,shopFreightCalcLog.getRegionCode());
        lqw.eq(shopFreightCalcLog.getTotalAmount() != null, ShopFreightCalcLog::getTotalAmount ,shopFreightCalcLog.getTotalAmount());
        lqw.eq(shopFreightCalcLog.getTotalQuantity() != null, ShopFreightCalcLog::getTotalQuantity ,shopFreightCalcLog.getTotalQuantity());
        lqw.eq(shopFreightCalcLog.getTotalWeight() != null, ShopFreightCalcLog::getTotalWeight ,shopFreightCalcLog.getTotalWeight());
        lqw.eq(shopFreightCalcLog.getTotalVolume() != null, ShopFreightCalcLog::getTotalVolume ,shopFreightCalcLog.getTotalVolume());
        lqw.eq(shopFreightCalcLog.getCalculatedFreight() != null, ShopFreightCalcLog::getCalculatedFreight ,shopFreightCalcLog.getCalculatedFreight());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getTemplateIds()),ShopFreightCalcLog::getTemplateIds ,shopFreightCalcLog.getTemplateIds());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getCalcDetail()),ShopFreightCalcLog::getCalcDetail ,shopFreightCalcLog.getCalcDetail());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopFreightCalcLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopFreightCalcLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopFreightCalcLog::getId);
        List<ShopFreightCalcLog> list = shopFreightCalcLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取运费计算日志表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopFreightCalcLog::getInfo", name = "获取 运费计算日志表 详细信息")
    public R<ShopFreightCalcLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopFreightCalcLogService.getById(id));
    }

    /**
     * 新增运费计算日志表
     */
    @PostMapping
    @Node(value = "shopFreightCalcLog::add", name = "新增 运费计算日志表")
    public R<Boolean> add(@RequestBody ShopFreightCalcLog shopFreightCalcLog) {
        return R.success(shopFreightCalcLogService.save(shopFreightCalcLog));
    }

    /**
     * 修改运费计算日志表
     */
    @PutMapping
    @Node(value = "shopFreightCalcLog::edit", name = "修改 运费计算日志表")
    public R<Boolean> edit(@RequestBody ShopFreightCalcLog shopFreightCalcLog) {
        return R.success(shopFreightCalcLogService.updateById(shopFreightCalcLog));
    }

    /**
     * 删除运费计算日志表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopFreightCalcLog::remove", name = "删除 运费计算日志表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopFreightCalcLogService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 运费计算日志表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopFreightCalcLog.class)
    @Node(value = "shopFreightCalcLog::exportExcel", name = "导出 运费计算日志表 Excel")
    public Object exportExcel(ShopFreightCalcLog shopFreightCalcLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopFreightCalcLog> lqw = new LambdaQueryWrapper<ShopFreightCalcLog>();
        lqw.eq(shopFreightCalcLog.getId() != null, ShopFreightCalcLog::getId ,shopFreightCalcLog.getId());
        lqw.eq(shopFreightCalcLog.getOrderId() != null, ShopFreightCalcLog::getOrderId ,shopFreightCalcLog.getOrderId());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getSessionId()),ShopFreightCalcLog::getSessionId ,shopFreightCalcLog.getSessionId());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getRegionCode()),ShopFreightCalcLog::getRegionCode ,shopFreightCalcLog.getRegionCode());
        lqw.eq(shopFreightCalcLog.getTotalAmount() != null, ShopFreightCalcLog::getTotalAmount ,shopFreightCalcLog.getTotalAmount());
        lqw.eq(shopFreightCalcLog.getTotalQuantity() != null, ShopFreightCalcLog::getTotalQuantity ,shopFreightCalcLog.getTotalQuantity());
        lqw.eq(shopFreightCalcLog.getTotalWeight() != null, ShopFreightCalcLog::getTotalWeight ,shopFreightCalcLog.getTotalWeight());
        lqw.eq(shopFreightCalcLog.getTotalVolume() != null, ShopFreightCalcLog::getTotalVolume ,shopFreightCalcLog.getTotalVolume());
        lqw.eq(shopFreightCalcLog.getCalculatedFreight() != null, ShopFreightCalcLog::getCalculatedFreight ,shopFreightCalcLog.getCalculatedFreight());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getTemplateIds()),ShopFreightCalcLog::getTemplateIds ,shopFreightCalcLog.getTemplateIds());
        lqw.like(StringUtils.isNotBlank(shopFreightCalcLog.getCalcDetail()),ShopFreightCalcLog::getCalcDetail ,shopFreightCalcLog.getCalcDetail());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopFreightCalcLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopFreightCalcLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopFreightCalcLog::getId);
        return shopFreightCalcLogService.list(lqw);
    }

    /**
     * 导入 运费计算日志表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopFreightCalcLog::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopFreightCalcLog.class, new ReadListener<ShopFreightCalcLog>() {
                @Override
                public void invoke(ShopFreightCalcLog shopFreightCalcLog, AnalysisContext analysisContext) {
                    shopFreightCalcLogService.save(shopFreightCalcLog);
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