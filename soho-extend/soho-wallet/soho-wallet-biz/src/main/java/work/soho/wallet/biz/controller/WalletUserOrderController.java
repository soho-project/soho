package work.soho.wallet.biz.controller;

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
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.wallet.biz.domain.WalletUserOrder;
import work.soho.wallet.biz.enums.WalletUserOrderEnums;
import work.soho.wallet.biz.service.WalletUserOrderService;

import java.util.Arrays;
import java.util.List;
/**
 * 用户支付单Controller
 *
 * @author fang
 */
@Api(tags = "用户支付单")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletUserOrder" )
public class WalletUserOrderController {

    private final WalletUserOrderService walletUserOrderService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询用户支付单列表
     */
    @GetMapping("/list")
    @Node(value = "walletUserOrder::list", name = "获取 用户支付单 列表")
    public R<PageSerializable<WalletUserOrder>> list(WalletUserOrder walletUserOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletUserOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletUserOrder.getId() != null, WalletUserOrder::getId ,walletUserOrder.getId());
        lqw.eq(walletUserOrder.getWalletId() != null, WalletUserOrder::getWalletId ,walletUserOrder.getWalletId());
        lqw.like(StringUtils.isNotBlank(walletUserOrder.getNo()),WalletUserOrder::getNo ,walletUserOrder.getNo());
//        lqw.like(StringUtils.isNotBlank(walletUserOrder.getAmount()),WalletUserOrder::getAmount ,walletUserOrder.getAmount());
        lqw.like(StringUtils.isNotBlank(walletUserOrder.getOutTrackingNumber()),WalletUserOrder::getOutTrackingNumber ,walletUserOrder.getOutTrackingNumber());
        lqw.eq(walletUserOrder.getStatus() != null, WalletUserOrder::getStatus ,walletUserOrder.getStatus());
        lqw.eq(walletUserOrder.getUpdatedTime() != null, WalletUserOrder::getUpdatedTime ,walletUserOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletUserOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletUserOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletUserOrder::getId);
        List<WalletUserOrder> list = walletUserOrderService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户支付单详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletUserOrder::getInfo", name = "获取 用户支付单 详细信息")
    public R<WalletUserOrder> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletUserOrderService.getById(id));
    }

    /**
     * 获取用户支付单详细信息
     */
    @GetMapping("/getByNo/{no}")
    public R<WalletUserOrder> getByNo(String no) {
        LambdaQueryWrapper<WalletUserOrder> lqw = new LambdaQueryWrapper<WalletUserOrder>();
        lqw.eq(WalletUserOrder::getNo, no);
        lqw.eq(WalletUserOrder::getStatus, WalletUserOrderEnums.Status.PENDING_PAYMENT.getId());
        return R.success(walletUserOrderService.getOne(lqw));
    }

    /**
     * 新增用户支付单
     */
    @PostMapping
    @Node(value = "walletUserOrder::add", name = "新增 用户支付单")
    public R<Boolean> add(@RequestBody WalletUserOrder walletUserOrder) {
        return R.success(walletUserOrderService.save(walletUserOrder));
    }

    /**
     * 修改用户支付单
     */
    @PutMapping
    @Node(value = "walletUserOrder::edit", name = "修改 用户支付单")
    public R<Boolean> edit(@RequestBody WalletUserOrder walletUserOrder) {
        return R.success(walletUserOrderService.updateById(walletUserOrder));
    }

    /**
     * 删除用户支付单
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletUserOrder::remove", name = "删除 用户支付单")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletUserOrderService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 用户支付单 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletUserOrder.class)
    @Node(value = "walletUserOrder::exportExcel", name = "导出 用户支付单 Excel")
    public Object exportExcel(WalletUserOrder walletUserOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletUserOrder> lqw = new LambdaQueryWrapper<WalletUserOrder>();
        lqw.eq(walletUserOrder.getId() != null, WalletUserOrder::getId ,walletUserOrder.getId());
        lqw.eq(walletUserOrder.getWalletId() != null, WalletUserOrder::getWalletId ,walletUserOrder.getWalletId());
        lqw.like(StringUtils.isNotBlank(walletUserOrder.getNo()),WalletUserOrder::getNo ,walletUserOrder.getNo());
//        lqw.like(StringUtils.isNotBlank(walletUserOrder.getAmount()),WalletUserOrder::getAmount ,walletUserOrder.getAmount());
        lqw.like(StringUtils.isNotBlank(walletUserOrder.getOutTrackingNumber()),WalletUserOrder::getOutTrackingNumber ,walletUserOrder.getOutTrackingNumber());
        lqw.eq(walletUserOrder.getStatus() != null, WalletUserOrder::getStatus ,walletUserOrder.getStatus());
        lqw.eq(walletUserOrder.getUpdatedTime() != null, WalletUserOrder::getUpdatedTime ,walletUserOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletUserOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletUserOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletUserOrder::getId);
        return walletUserOrderService.list(lqw);
    }

    /**
     * 导入 用户支付单 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletUserOrder::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletUserOrder.class, new ReadListener<WalletUserOrder>() {
                @Override
                public void invoke(WalletUserOrder walletUserOrder, AnalysisContext analysisContext) {
                    walletUserOrderService.save(walletUserOrder);
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