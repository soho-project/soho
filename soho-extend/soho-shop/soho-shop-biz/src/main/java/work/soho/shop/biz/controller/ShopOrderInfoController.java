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
import work.soho.shop.biz.domain.ShopOrderInfo;
import work.soho.shop.biz.service.ShopOrderInfoService;

import java.util.Arrays;
import java.util.List;
/**
 * 订单Controller
 *
 * @author fang
 */
@Api(tags = "订单")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopOrderInfo" )
public class ShopOrderInfoController {

    private final ShopOrderInfoService shopOrderInfoService;

    /**
     * 查询订单列表
     */
    @GetMapping("/list")
    @Node(value = "shopOrderInfo::list", name = "获取 订单 列表")
    public R<PageSerializable<ShopOrderInfo>> list(ShopOrderInfo shopOrderInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopOrderInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopOrderInfo.getId() != null, ShopOrderInfo::getId ,shopOrderInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getNo()),ShopOrderInfo::getNo ,shopOrderInfo.getNo());
        lqw.eq(shopOrderInfo.getUserId() != null, ShopOrderInfo::getUserId ,shopOrderInfo.getUserId());
        lqw.eq(shopOrderInfo.getAmount() != null, ShopOrderInfo::getAmount ,shopOrderInfo.getAmount());
        lqw.eq(shopOrderInfo.getStatus() != null, ShopOrderInfo::getStatus ,shopOrderInfo.getStatus());
        lqw.eq(shopOrderInfo.getPayStatus() != null, ShopOrderInfo::getPayStatus ,shopOrderInfo.getPayStatus());
        lqw.eq(shopOrderInfo.getFreightStatus() != null, ShopOrderInfo::getFreightStatus ,shopOrderInfo.getFreightStatus());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getReceivingAddress()),ShopOrderInfo::getReceivingAddress ,shopOrderInfo.getReceivingAddress());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getConsignee()),ShopOrderInfo::getConsignee ,shopOrderInfo.getConsignee());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getReceivingPhoneNumber()),ShopOrderInfo::getReceivingPhoneNumber ,shopOrderInfo.getReceivingPhoneNumber());
        lqw.eq(shopOrderInfo.getUpdatedTime() != null, ShopOrderInfo::getUpdatedTime ,shopOrderInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopOrderInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopOrderInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopOrderInfo::getId);
        List<ShopOrderInfo> list = shopOrderInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取订单详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopOrderInfo::getInfo", name = "获取 订单 详细信息")
    public R<ShopOrderInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopOrderInfoService.getById(id));
    }

    /**
     * 新增订单
     */
    @PostMapping
    @Node(value = "shopOrderInfo::add", name = "新增 订单")
    public R<Boolean> add(@RequestBody ShopOrderInfo shopOrderInfo) {
        return R.success(shopOrderInfoService.save(shopOrderInfo));
    }

    /**
     * 修改订单
     */
    @PutMapping
    @Node(value = "shopOrderInfo::edit", name = "修改 订单")
    public R<Boolean> edit(@RequestBody ShopOrderInfo shopOrderInfo) {
        return R.success(shopOrderInfoService.updateById(shopOrderInfo));
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopOrderInfo::remove", name = "删除 订单")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopOrderInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 订单 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopOrderInfo.class)
    @Node(value = "shopOrderInfo::exportExcel", name = "导出 订单 Excel")
    public Object exportExcel(ShopOrderInfo shopOrderInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopOrderInfo> lqw = new LambdaQueryWrapper<ShopOrderInfo>();
        lqw.eq(shopOrderInfo.getId() != null, ShopOrderInfo::getId ,shopOrderInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getNo()),ShopOrderInfo::getNo ,shopOrderInfo.getNo());
        lqw.eq(shopOrderInfo.getUserId() != null, ShopOrderInfo::getUserId ,shopOrderInfo.getUserId());
        lqw.eq(shopOrderInfo.getAmount() != null, ShopOrderInfo::getAmount ,shopOrderInfo.getAmount());
        lqw.eq(shopOrderInfo.getStatus() != null, ShopOrderInfo::getStatus ,shopOrderInfo.getStatus());
        lqw.eq(shopOrderInfo.getPayStatus() != null, ShopOrderInfo::getPayStatus ,shopOrderInfo.getPayStatus());
        lqw.eq(shopOrderInfo.getFreightStatus() != null, ShopOrderInfo::getFreightStatus ,shopOrderInfo.getFreightStatus());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getReceivingAddress()),ShopOrderInfo::getReceivingAddress ,shopOrderInfo.getReceivingAddress());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getConsignee()),ShopOrderInfo::getConsignee ,shopOrderInfo.getConsignee());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getReceivingPhoneNumber()),ShopOrderInfo::getReceivingPhoneNumber ,shopOrderInfo.getReceivingPhoneNumber());
        lqw.eq(shopOrderInfo.getUpdatedTime() != null, ShopOrderInfo::getUpdatedTime ,shopOrderInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopOrderInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopOrderInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopOrderInfo::getId);
        return shopOrderInfoService.list(lqw);
    }

    /**
     * 导入 订单 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopOrderInfo::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopOrderInfo.class, new ReadListener<ShopOrderInfo>() {
                @Override
                public void invoke(ShopOrderInfo shopOrderInfo, AnalysisContext analysisContext) {
                    shopOrderInfoService.save(shopOrderInfo);
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