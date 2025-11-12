package work.soho.wallet.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
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
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletWithdrawalOrder;
import work.soho.wallet.biz.service.WalletInfoService;

/**
 * Controller
 *
 * @author fang
 */
@Api(tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletInfo" )
public class WalletInfoController {

    private final WalletInfoService walletInfoService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "walletInfo::list", name = "获取  列表")
    public R<PageSerializable<WalletInfo>> list(WalletInfo walletInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletInfo.getId() != null, WalletInfo::getId ,walletInfo.getId());
        lqw.eq(walletInfo.getUserId() != null, WalletInfo::getUserId ,walletInfo.getUserId());
        lqw.eq(walletInfo.getType() != null, WalletInfo::getType ,walletInfo.getType());
        lqw.eq(walletInfo.getAmount() != null, WalletInfo::getAmount ,walletInfo.getAmount());
        lqw.eq(walletInfo.getStatus() != null, WalletInfo::getStatus ,walletInfo.getStatus());
        lqw.eq(walletInfo.getUpdatedTime() != null, WalletInfo::getUpdatedTime ,walletInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletInfo::getCreatedTime);
        List<WalletInfo> list = walletInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletInfo::getInfo", name = "获取  详细信息")
    public R<WalletInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletInfoService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "walletInfo::add", name = "新增 ")
    public R<Boolean> add(@RequestBody WalletInfo walletInfo) {
        return R.success(walletInfoService.save(walletInfo));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "walletInfo::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody WalletInfo walletInfo, @AuthenticationPrincipal SohoUserDetails userDetails) {
        WalletInfo oldWallet = walletInfoService.getById(walletInfo.getId());
        //检查金额是否有修改
        if (oldWallet.getAmount().compareTo(walletInfo.getAmount()) != 0) {
            //金额有修改，需要更新余额
            walletInfoService.updateAmount(oldWallet, walletInfo.getAmount().subtract(oldWallet.getAmount()), "账户金额修改; 操作员：" + userDetails.getId());
            // 避免覆盖金额，必须置为null
            walletInfo.setAmount(null);
        }
        return R.success(walletInfoService.updateById(walletInfo));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletInfo::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletInfoService.removeByIds(Arrays.asList(ids)));
    }

//    /**
//     * 获取  枚举选项
//     *
//     * @return
//     */
//    @GetMapping("/queryStatusEnumOption")
//    public R<List<OptionVo<Integer, String>>> statusEnumOption() {
//        return R.success(adminDictApiService.getOptionsByCode("walletInfo-status"));
//    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletInfo.class)
    @Node(value = "walletInfo::exportExcel", name = "导出  Excel")
    public Object exportExcel(WalletInfo walletInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletInfo> lqw = new LambdaQueryWrapper<WalletInfo>();
        lqw.eq(walletInfo.getId() != null, WalletInfo::getId ,walletInfo.getId());
        lqw.eq(walletInfo.getUserId() != null, WalletInfo::getUserId ,walletInfo.getUserId());
        lqw.eq(walletInfo.getType() != null, WalletInfo::getType ,walletInfo.getType());
        lqw.eq(walletInfo.getAmount() != null, WalletInfo::getAmount ,walletInfo.getAmount());
        lqw.eq(walletInfo.getStatus() != null, WalletInfo::getStatus ,walletInfo.getStatus());
        lqw.eq(walletInfo.getUpdatedTime() != null, WalletInfo::getUpdatedTime ,walletInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return walletInfoService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletInfo::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletInfo.class, new ReadListener<WalletInfo>() {
                @Override
                public void invoke(WalletInfo walletInfo, AnalysisContext analysisContext) {
                    walletInfoService.save(walletInfo);
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