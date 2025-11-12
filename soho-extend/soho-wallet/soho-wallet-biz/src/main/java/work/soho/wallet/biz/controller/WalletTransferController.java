package work.soho.wallet.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import work.soho.common.core.util.PageUtils;
import work.soho.common.security.utils.SecurityUtils;
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
import work.soho.wallet.biz.domain.WalletTransfer;
import work.soho.wallet.biz.service.WalletTransferService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 钱包转账Controller
 *
 * @author fang
 */
@Api(tags = "钱包转账")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletTransfer" )
public class WalletTransferController {

    private final WalletTransferService walletTransferService;


    /**
     * 查询钱包转账列表
     */
    @GetMapping("/list")
    @Node(value = "walletTransfer::list", name = "获取 钱包转账 列表")
    public R<PageSerializable<WalletTransfer>> list(WalletTransfer walletTransfer, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletTransfer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletTransfer.getId() != null, WalletTransfer::getId ,walletTransfer.getId());
        lqw.like(StringUtils.isNotBlank(walletTransfer.getCode()),WalletTransfer::getCode ,walletTransfer.getCode());
        lqw.eq(walletTransfer.getFromUserId() != null, WalletTransfer::getFromUserId ,walletTransfer.getFromUserId());
        lqw.eq(walletTransfer.getFromWalletId() != null, WalletTransfer::getFromWalletId ,walletTransfer.getFromWalletId());
        lqw.eq(walletTransfer.getFromWalletType() != null, WalletTransfer::getFromWalletType ,walletTransfer.getFromWalletType());
        lqw.eq(walletTransfer.getFromAmount() != null, WalletTransfer::getFromAmount ,walletTransfer.getFromAmount());
        lqw.eq(walletTransfer.getToWalletId() != null, WalletTransfer::getToWalletId ,walletTransfer.getToWalletId());
        lqw.eq(walletTransfer.getToWalletType() != null, WalletTransfer::getToWalletType ,walletTransfer.getToWalletType());
        lqw.eq(walletTransfer.getToUserId() != null,WalletTransfer::getToUserId ,walletTransfer.getToUserId());
        lqw.eq(walletTransfer.getToAmount() != null, WalletTransfer::getToAmount ,walletTransfer.getToAmount());
        lqw.like(StringUtils.isNotBlank(walletTransfer.getRemark()),WalletTransfer::getRemark ,walletTransfer.getRemark());
//        lqw.like(StringUtils.isNotBlank(walletTransfer.getUpdatedTime()),WalletTransfer::getUpdatedTime ,walletTransfer.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletTransfer::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletTransfer::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletTransfer> list = walletTransferService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包转账详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletTransfer::getInfo", name = "获取 钱包转账 详细信息")
    public R<WalletTransfer> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletTransferService.getById(id));
    }

    /**
     * 新增钱包转账
     */
    @PostMapping
    @Node(value = "walletTransfer::add", name = "新增 钱包转账")
    public R<Boolean> add(@RequestBody WalletTransfer walletTransfer) {
        return R.success(walletTransferService.save(walletTransfer));
    }

    /**
     * 修改钱包转账
     */
    @PutMapping
    @Node(value = "walletTransfer::edit", name = "修改 钱包转账")
    public R<Boolean> edit(@RequestBody WalletTransfer walletTransfer) {
        return R.success(walletTransferService.updateById(walletTransfer));
    }

    /**
     * 删除钱包转账
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletTransfer::remove", name = "删除 钱包转账")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletTransferService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 钱包转账 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletTransfer.class)
    @Node(value = "walletTransfer::exportExcel", name = "导出 钱包转账 Excel")
    public Object exportExcel(WalletTransfer walletTransfer, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletTransfer> lqw = new LambdaQueryWrapper<WalletTransfer>();
        lqw.eq(walletTransfer.getId() != null, WalletTransfer::getId ,walletTransfer.getId());
        lqw.like(StringUtils.isNotBlank(walletTransfer.getCode()),WalletTransfer::getCode ,walletTransfer.getCode());
        lqw.eq(walletTransfer.getFromUserId() != null, WalletTransfer::getFromUserId ,walletTransfer.getFromUserId());
        lqw.eq(walletTransfer.getFromWalletId() != null, WalletTransfer::getFromWalletId ,walletTransfer.getFromWalletId());
        lqw.eq(walletTransfer.getFromWalletType() != null, WalletTransfer::getFromWalletType ,walletTransfer.getFromWalletType());
        lqw.eq(walletTransfer.getFromAmount() != null, WalletTransfer::getFromAmount ,walletTransfer.getFromAmount());
        lqw.eq(walletTransfer.getToWalletId() != null, WalletTransfer::getToWalletId ,walletTransfer.getToWalletId());
        lqw.eq(walletTransfer.getToWalletType() != null, WalletTransfer::getToWalletType ,walletTransfer.getToWalletType());
        lqw.eq(walletTransfer.getToUserId()!=null,WalletTransfer::getToUserId ,walletTransfer.getToUserId());
        lqw.eq(walletTransfer.getToAmount() != null, WalletTransfer::getToAmount ,walletTransfer.getToAmount());
        lqw.like(StringUtils.isNotBlank(walletTransfer.getRemark()),WalletTransfer::getRemark ,walletTransfer.getRemark());
//        lqw.like(StringUtils.isNotBlank(walletTransfer.getUpdatedTime()),WalletTransfer::getUpdatedTime ,walletTransfer.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletTransfer::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletTransfer::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return walletTransferService.list(lqw);
    }

    /**
     * 导入 钱包转账 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletTransfer::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletTransfer.class, new ReadListener<WalletTransfer>() {
                @Override
                public void invoke(WalletTransfer walletTransfer, AnalysisContext analysisContext) {
                    walletTransferService.save(walletTransfer);
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