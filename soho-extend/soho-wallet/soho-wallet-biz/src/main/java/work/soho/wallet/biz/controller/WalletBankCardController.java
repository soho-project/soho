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
import work.soho.wallet.biz.domain.WalletBankCard;
import work.soho.wallet.biz.domain.WalletWithdrawalOrder;
import work.soho.wallet.biz.service.WalletBankCardService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 用户钱包银行卡信息表Controller
 *
 * @author fang
 */
@Api(tags = "用户钱包银行卡信息表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/admin/walletBankCard" )
public class WalletBankCardController {

    private final WalletBankCardService walletBankCardService;
//    private final AdminDictApiService adminDictApiService;

    /**
     * 查询用户钱包银行卡信息表列表
     */
    @GetMapping("/list")
    @Node(value = "walletBankCard::list", name = "获取 用户钱包银行卡信息表 列表")
    public R<PageSerializable<WalletBankCard>> list(WalletBankCard walletBankCard, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletBankCard> lqw = new LambdaQueryWrapper<>();
        lqw.eq(walletBankCard.getId() != null, WalletBankCard::getId ,walletBankCard.getId());
        lqw.eq(walletBankCard.getUserId() != null, WalletBankCard::getUserId ,walletBankCard.getUserId());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getName()),WalletBankCard::getName ,walletBankCard.getName());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getIdCode()),WalletBankCard::getIdCode ,walletBankCard.getIdCode());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getCardCode()),WalletBankCard::getCardCode ,walletBankCard.getCardCode());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getPhone()),WalletBankCard::getPhone ,walletBankCard.getPhone());
        lqw.eq(walletBankCard.getUpdatedTime() != null, WalletBankCard::getUpdatedTime ,walletBankCard.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletBankCard::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletBankCard::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(WalletBankCard::getCreatedTime);
        List<WalletBankCard> list = walletBankCardService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户钱包银行卡信息表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "walletBankCard::getInfo", name = "获取 用户钱包银行卡信息表 详细信息")
    public R<WalletBankCard> getInfo(@PathVariable("id" ) Long id) {
        return R.success(walletBankCardService.getById(id));
    }

    /**
     * 新增用户钱包银行卡信息表
     */
    @PostMapping
    @Node(value = "walletBankCard::add", name = "新增 用户钱包银行卡信息表")
    public R<Boolean> add(@RequestBody WalletBankCard walletBankCard) {
        return R.success(walletBankCardService.save(walletBankCard));
    }

    /**
     * 修改用户钱包银行卡信息表
     */
    @PutMapping
    @Node(value = "walletBankCard::edit", name = "修改 用户钱包银行卡信息表")
    public R<Boolean> edit(@RequestBody WalletBankCard walletBankCard) {
        return R.success(walletBankCardService.updateById(walletBankCard));
    }

    /**
     * 删除用户钱包银行卡信息表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "walletBankCard::remove", name = "删除 用户钱包银行卡信息表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletBankCardService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 用户钱包银行卡信息表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = WalletBankCard.class)
    @Node(value = "walletBankCard::exportExcel", name = "导出 用户钱包银行卡信息表 Excel")
    public Object exportExcel(WalletBankCard walletBankCard, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<WalletBankCard> lqw = new LambdaQueryWrapper<WalletBankCard>();
        lqw.eq(walletBankCard.getId() != null, WalletBankCard::getId ,walletBankCard.getId());
        lqw.eq(walletBankCard.getUserId() != null, WalletBankCard::getUserId ,walletBankCard.getUserId());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getName()),WalletBankCard::getName ,walletBankCard.getName());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getIdCode()),WalletBankCard::getIdCode ,walletBankCard.getIdCode());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getCardCode()),WalletBankCard::getCardCode ,walletBankCard.getCardCode());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getPhone()),WalletBankCard::getPhone ,walletBankCard.getPhone());
        lqw.eq(walletBankCard.getUpdatedTime() != null, WalletBankCard::getUpdatedTime ,walletBankCard.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletBankCard::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletBankCard::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return walletBankCardService.list(lqw);
    }

    /**
     * 导入 用户钱包银行卡信息表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "walletBankCard::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), WalletBankCard.class, new ReadListener<WalletBankCard>() {
                @Override
                public void invoke(WalletBankCard walletBankCard, AnalysisContext analysisContext) {
                    walletBankCardService.save(walletBankCard);
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