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
import work.soho.wallet.biz.domain.WalletRecharge;
import work.soho.wallet.biz.service.WalletRechargeService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 钱包充值Controller
 *
 * @author fang
 */
@Api(tags = "钱包充值")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletRecharge" )
public class WalletRechargeController {

    private final WalletRechargeService walletRechargeService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询钱包充值列表
     */
    @GetMapping("/list")
    @Node(value = "walletRecharge::list", name = "获取 钱包充值 列表")
    public R<PageSerializable<WalletRecharge>> list(WalletRecharge walletRecharge, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletRecharge> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletRecharge.getId() != null, WalletRecharge::getId ,walletRecharge.getId());
        lqw.like(StringUtils.isNotBlank(walletRecharge.getCode()),WalletRecharge::getCode ,walletRecharge.getCode());
        lqw.eq(walletRecharge.getAmount() != null, WalletRecharge::getAmount ,walletRecharge.getAmount());
        lqw.eq(walletRecharge.getUserId() != null, WalletRecharge::getUserId ,walletRecharge.getUserId());
        lqw.eq(walletRecharge.getWalletId() != null, WalletRecharge::getWalletId ,walletRecharge.getWalletId());
        lqw.eq(walletRecharge.getStatus() != null, WalletRecharge::getStatus ,walletRecharge.getStatus());
        lqw.eq(walletRecharge.getUpdatedTime() != null, WalletRecharge::getUpdatedTime ,walletRecharge.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletRecharge::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletRecharge::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletRecharge::getCreatedTime);
        List<WalletRecharge> list = walletRechargeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包充值详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletRecharge::getInfo", name = "获取 钱包充值 详细信息")
    public R<WalletRecharge> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletRechargeService.getById(id));
    }

    /**
     * 新增钱包充值
     */
    @PostMapping
    @Node(value = "walletRecharge::add", name = "新增 钱包充值")
    public R<Boolean> add(@RequestBody WalletRecharge walletRecharge) {
        return R.success(walletRechargeService.save(walletRecharge));
    }

    /**
     * 修改钱包充值
     */
    @PutMapping
    @Node(value = "walletRecharge::edit", name = "修改 钱包充值")
    public R<Boolean> edit(@RequestBody WalletRecharge walletRecharge) {
        return R.success(walletRechargeService.updateById(walletRecharge));
    }

    /**
     * 删除钱包充值
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletRecharge::remove", name = "删除 钱包充值")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletRechargeService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 钱包充值 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletRecharge.class)
    @Node(value = "walletRecharge::exportExcel", name = "导出 钱包充值 Excel")
    public Object exportExcel(WalletRecharge walletRecharge, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletRecharge> lqw = new LambdaQueryWrapper<WalletRecharge>();
        lqw.eq(walletRecharge.getId() != null, WalletRecharge::getId ,walletRecharge.getId());
        lqw.like(StringUtils.isNotBlank(walletRecharge.getCode()),WalletRecharge::getCode ,walletRecharge.getCode());
        lqw.eq(walletRecharge.getAmount() != null, WalletRecharge::getAmount ,walletRecharge.getAmount());
        lqw.eq(walletRecharge.getUserId() != null, WalletRecharge::getUserId ,walletRecharge.getUserId());
        lqw.eq(walletRecharge.getWalletId() != null, WalletRecharge::getWalletId ,walletRecharge.getWalletId());
        lqw.eq(walletRecharge.getStatus() != null, WalletRecharge::getStatus ,walletRecharge.getStatus());
        lqw.eq(walletRecharge.getUpdatedTime() != null, WalletRecharge::getUpdatedTime ,walletRecharge.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletRecharge::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletRecharge::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return walletRechargeService.list(lqw);
    }

    /**
     * 导入 钱包充值 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletRecharge::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletRecharge.class, new ReadListener<WalletRecharge>() {
                @Override
                public void invoke(WalletRecharge walletRecharge, AnalysisContext analysisContext) {
                    walletRechargeService.save(walletRecharge);
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