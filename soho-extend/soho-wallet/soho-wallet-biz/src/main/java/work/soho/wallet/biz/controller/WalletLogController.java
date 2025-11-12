package work.soho.wallet.biz.controller;

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
import work.soho.wallet.biz.domain.WalletLog;
import work.soho.wallet.biz.service.WalletLogService;

import work.soho.admin.api.request.BetweenCreatedTimeRequest;

/**
 * 钱包变更日志Controller
 *
 * @author fang
 */
@Api(tags = "钱包变更日志")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletLog" )
public class WalletLogController {

    private final WalletLogService walletLogService;

    /**
     * 查询钱包变更日志列表
     */
    @GetMapping("/list")
    @Node(value = "walletLog::list", name = "获取 钱包变更日志 列表")
    public R<PageSerializable<WalletLog>> list(WalletLog walletLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletLog.getId() != null, WalletLog::getId ,walletLog.getId());
        lqw.eq(walletLog.getWalletId() != null, WalletLog::getWalletId ,walletLog.getWalletId());
        lqw.eq(walletLog.getAmount() != null, WalletLog::getAmount ,walletLog.getAmount());
        lqw.eq(walletLog.getBeforeAmount() != null, WalletLog::getBeforeAmount ,walletLog.getBeforeAmount());
        lqw.eq(walletLog.getAfterAmount() != null, WalletLog::getAfterAmount ,walletLog.getAfterAmount());
        lqw.like(StringUtils.isNotBlank(walletLog.getNotes()),WalletLog::getNotes ,walletLog.getNotes());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletLog::getId);
        List<WalletLog> list = walletLogService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包变更日志详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletLog::getInfo", name = "获取 钱包变更日志 详细信息")
    public R<WalletLog> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletLogService.getById(id));
    }

    /**
     * 新增钱包变更日志
     */
    @PostMapping
    @Node(value = "walletLog::add", name = "新增 钱包变更日志")
    public R<Boolean> add(@RequestBody WalletLog walletLog) {
        return R.success(walletLogService.save(walletLog));
    }

    /**
     * 修改钱包变更日志
     */
    @PutMapping
    @Node(value = "walletLog::edit", name = "修改 钱包变更日志")
    public R<Boolean> edit(@RequestBody WalletLog walletLog) {
        return R.success(walletLogService.updateById(walletLog));
    }

    /**
     * 删除钱包变更日志
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletLog::remove", name = "删除 钱包变更日志")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletLogService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 钱包变更日志 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletLog.class)
    @Node(value = "walletLog::exportExcel", name = "导出 钱包变更日志 Excel")
    public Object exportExcel(WalletLog walletLog, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletLog> lqw = new LambdaQueryWrapper<WalletLog>();
        lqw.eq(walletLog.getId() != null, WalletLog::getId ,walletLog.getId());
        lqw.eq(walletLog.getWalletId() != null, WalletLog::getWalletId ,walletLog.getWalletId());
        lqw.eq(walletLog.getAmount() != null, WalletLog::getAmount ,walletLog.getAmount());
        lqw.eq(walletLog.getBeforeAmount() != null, WalletLog::getBeforeAmount ,walletLog.getBeforeAmount());
        lqw.eq(walletLog.getAfterAmount() != null, WalletLog::getAfterAmount ,walletLog.getAfterAmount());
        lqw.like(StringUtils.isNotBlank(walletLog.getNotes()),WalletLog::getNotes ,walletLog.getNotes());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletLog::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletLog::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return walletLogService.list(lqw);
    }

    /**
     * 导入 钱包变更日志 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletLog::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletLog.class, new ReadListener<WalletLog>() {
                @Override
                public void invoke(WalletLog walletLog, AnalysisContext analysisContext) {
                    walletLogService.save(walletLog);
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