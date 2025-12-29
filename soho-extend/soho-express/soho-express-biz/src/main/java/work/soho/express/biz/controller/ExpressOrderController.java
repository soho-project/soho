package work.soho.express.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import com.zto.zop.response.ScanTraceDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.express.biz.domain.ExpressOrder;
import work.soho.express.biz.service.ExpressOrderService;

import java.util.Arrays;
import java.util.List;
/**
 * 快递单Controller
 *
 * @author fang
 */
@Api(tags = "快递单")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/express/admin/expressOrder" )
public class ExpressOrderController {

    private final ExpressOrderService expressOrderService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询快递单列表
     */
    @GetMapping("/list")
    @Node(value = "expressOrder::list", name = "获取 快递单 列表")
    public R<PageSerializable<ExpressOrder>> list(ExpressOrder expressOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExpressOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(expressOrder.getId() != null, ExpressOrder::getId ,expressOrder.getId());
        lqw.like(StringUtils.isNotBlank(expressOrder.getNo()),ExpressOrder::getNo ,expressOrder.getNo());
        lqw.eq(expressOrder.getStatus() != null, ExpressOrder::getStatus ,expressOrder.getStatus());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderName()),ExpressOrder::getSenderName ,expressOrder.getSenderName());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderPhone()),ExpressOrder::getSenderPhone ,expressOrder.getSenderPhone());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderMobile()),ExpressOrder::getSenderMobile ,expressOrder.getSenderMobile());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderProvince()),ExpressOrder::getSenderProvince ,expressOrder.getSenderProvince());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderCity()),ExpressOrder::getSenderCity ,expressOrder.getSenderCity());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderDistrict()),ExpressOrder::getSenderDistrict ,expressOrder.getSenderDistrict());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderAddress()),ExpressOrder::getSenderAddress ,expressOrder.getSenderAddress());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverName()),ExpressOrder::getReceiverName ,expressOrder.getReceiverName());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverPhone()),ExpressOrder::getReceiverPhone ,expressOrder.getReceiverPhone());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverMobile()),ExpressOrder::getReceiverMobile ,expressOrder.getReceiverMobile());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverProvince()),ExpressOrder::getReceiverProvince ,expressOrder.getReceiverProvince());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverCity()),ExpressOrder::getReceiverCity ,expressOrder.getReceiverCity());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverDistrict()),ExpressOrder::getReceiverDistrict ,expressOrder.getReceiverDistrict());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverAddress()),ExpressOrder::getReceiverAddress ,expressOrder.getReceiverAddress());
        lqw.like(StringUtils.isNotBlank(expressOrder.getOrderItems()),ExpressOrder::getOrderItems ,expressOrder.getOrderItems());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSummaryInfo()),ExpressOrder::getSummaryInfo ,expressOrder.getSummaryInfo());
        lqw.like(StringUtils.isNotBlank(expressOrder.getOrderVasList()),ExpressOrder::getOrderVasList ,expressOrder.getOrderVasList());
        lqw.like(StringUtils.isNotBlank(expressOrder.getRemark()),ExpressOrder::getRemark ,expressOrder.getRemark());
        lqw.like(StringUtils.isNotBlank(expressOrder.getBillCode()),ExpressOrder::getBillCode ,expressOrder.getBillCode());
        lqw.like(StringUtils.isNotBlank(expressOrder.getPartnerOrderNo()),ExpressOrder::getPartnerOrderNo ,expressOrder.getPartnerOrderNo());
        lqw.eq(expressOrder.getExpressInfoId() != null, ExpressOrder::getExpressInfoId ,expressOrder.getExpressInfoId());
        lqw.eq(expressOrder.getUpdatedTime() != null, ExpressOrder::getUpdatedTime ,expressOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExpressOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExpressOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ExpressOrder::getId);
        List<ExpressOrder> list = expressOrderService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    @GetMapping("/send/{id}")
    @Node(value = "expressOrder::send", name = "发货")
    public R<Boolean> send(@PathVariable("id") Long id) {
        expressOrderService.push(id, 1L);
        return R.success();
    }

    @GetMapping("/cancel/{id}")
    @Node(value = "expressOrder::send", name = "发货")
    public R<Boolean> cancel(@PathVariable("id") Long id) {
        return R.success(expressOrderService.cancel(id));
    }

    @GetMapping("/intercept/{id}")
    @Node(value = "expressOrder::intercept", name = "拦截")
    public R<Boolean> intercept(@PathVariable("id") Long id) {
        return R.success(expressOrderService.intercept(id));
    }

    @GetMapping("/queryTrack/{id}")
    @Node(value = "expressOrder::queryTrack", name = "查询轨迹")
    public R<List<ScanTraceDTO>> queryTrack(@PathVariable("id") Long id) {
        return R.success(expressOrderService.queryTrack(id));
    }

    @GetMapping("/interceptSuccess/{id}")
    @Node(value = "expressOrder::interceptSuccess", name = "拦截成功")
    public R<Boolean> interceptSuccess(@PathVariable("id") Long id) {
        return R.success(expressOrderService.interceptSuccess(id));
    }

    /**
     * 获取快递单详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "expressOrder::getInfo", name = "获取 快递单 详细信息")
    public R<ExpressOrder> getInfo(@PathVariable("id" ) Long id) {
        return R.success(expressOrderService.getById(id));
    }

    /**
     * 新增快递单
     */
    @PostMapping
    @Node(value = "expressOrder::add", name = "新增 快递单")
    public R<Boolean> add(@RequestBody ExpressOrder expressOrder) {
        return R.success(expressOrderService.save(expressOrder));
    }

    /**
     * 修改快递单
     */
    @PutMapping
    @Node(value = "expressOrder::edit", name = "修改 快递单")
    public R<Boolean> edit(@RequestBody ExpressOrder expressOrder) {
        return R.success(expressOrderService.updateById(expressOrder));
    }

    /**
     * 删除快递单
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "expressOrder::remove", name = "删除 快递单")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(expressOrderService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 快递单 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ExpressOrder.class)
    @Node(value = "expressOrder::exportExcel", name = "导出 快递单 Excel")
    public Object exportExcel(ExpressOrder expressOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ExpressOrder> lqw = new LambdaQueryWrapper<ExpressOrder>();
        lqw.eq(expressOrder.getId() != null, ExpressOrder::getId ,expressOrder.getId());
        lqw.like(StringUtils.isNotBlank(expressOrder.getNo()),ExpressOrder::getNo ,expressOrder.getNo());
        lqw.eq(expressOrder.getStatus() != null, ExpressOrder::getStatus ,expressOrder.getStatus());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderName()),ExpressOrder::getSenderName ,expressOrder.getSenderName());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderPhone()),ExpressOrder::getSenderPhone ,expressOrder.getSenderPhone());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderMobile()),ExpressOrder::getSenderMobile ,expressOrder.getSenderMobile());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderProvince()),ExpressOrder::getSenderProvince ,expressOrder.getSenderProvince());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderCity()),ExpressOrder::getSenderCity ,expressOrder.getSenderCity());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderDistrict()),ExpressOrder::getSenderDistrict ,expressOrder.getSenderDistrict());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSenderAddress()),ExpressOrder::getSenderAddress ,expressOrder.getSenderAddress());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverName()),ExpressOrder::getReceiverName ,expressOrder.getReceiverName());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverPhone()),ExpressOrder::getReceiverPhone ,expressOrder.getReceiverPhone());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverMobile()),ExpressOrder::getReceiverMobile ,expressOrder.getReceiverMobile());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverProvince()),ExpressOrder::getReceiverProvince ,expressOrder.getReceiverProvince());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverCity()),ExpressOrder::getReceiverCity ,expressOrder.getReceiverCity());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverDistrict()),ExpressOrder::getReceiverDistrict ,expressOrder.getReceiverDistrict());
        lqw.like(StringUtils.isNotBlank(expressOrder.getReceiverAddress()),ExpressOrder::getReceiverAddress ,expressOrder.getReceiverAddress());
        lqw.like(StringUtils.isNotBlank(expressOrder.getOrderItems()),ExpressOrder::getOrderItems ,expressOrder.getOrderItems());
        lqw.like(StringUtils.isNotBlank(expressOrder.getSummaryInfo()),ExpressOrder::getSummaryInfo ,expressOrder.getSummaryInfo());
        lqw.like(StringUtils.isNotBlank(expressOrder.getOrderVasList()),ExpressOrder::getOrderVasList ,expressOrder.getOrderVasList());
        lqw.like(StringUtils.isNotBlank(expressOrder.getRemark()),ExpressOrder::getRemark ,expressOrder.getRemark());
        lqw.like(StringUtils.isNotBlank(expressOrder.getBillCode()),ExpressOrder::getBillCode ,expressOrder.getBillCode());
        lqw.like(StringUtils.isNotBlank(expressOrder.getPartnerOrderNo()),ExpressOrder::getPartnerOrderNo ,expressOrder.getPartnerOrderNo());
        lqw.eq(expressOrder.getUpdatedTime() != null, ExpressOrder::getUpdatedTime ,expressOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExpressOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExpressOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ExpressOrder::getId);
        return expressOrderService.list(lqw);
    }

    /**
     * 导入 快递单 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "expressOrder::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ExpressOrder.class, new ReadListener<ExpressOrder>() {
                @Override
                public void invoke(ExpressOrder expressOrder, AnalysisContext analysisContext) {
                    expressOrderService.save(expressOrder);
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