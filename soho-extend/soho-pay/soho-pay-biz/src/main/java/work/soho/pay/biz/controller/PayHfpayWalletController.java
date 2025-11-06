package work.soho.pay.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.pay.biz.domain.PayHfpayWallet;
import work.soho.pay.biz.service.PayHfpayWalletService;

import java.util.Arrays;
import java.util.List;

/**
 * 汇付通钱包信息Controller
 *
 * @author fang
 */
@Api(tags = "汇付通钱包信息")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay/admin/payHfpayWallet" )
public class PayHfpayWalletController {

    private final PayHfpayWalletService payHfpayWalletService;

    /**
     * 查询汇付通钱包信息列表
     */
    @GetMapping("/list")
    @Node(value = "payHfpayWallet::list", name = "获取 汇付通钱包信息 列表")
    public R<PageSerializable<PayHfpayWallet>> list(PayHfpayWallet payHfpayWallet, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<PayHfpayWallet> lqw = new LambdaQueryWrapper<>();
        lqw.eq(payHfpayWallet.getId() != null, PayHfpayWallet::getId ,payHfpayWallet.getId());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getOrderCode()),PayHfpayWallet::getOrderCode ,payHfpayWallet.getOrderCode());
        lqw.eq(payHfpayWallet.getUserId() != null, PayHfpayWallet::getUserId ,payHfpayWallet.getUserId());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getUserCustId()),PayHfpayWallet::getUserCustId ,payHfpayWallet.getUserCustId());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getAcctId()),PayHfpayWallet::getAcctId ,payHfpayWallet.getAcctId());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getFeeCustId()),PayHfpayWallet::getFeeCustId ,payHfpayWallet.getFeeCustId());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getFeeAcctId()),PayHfpayWallet::getFeeAcctId ,payHfpayWallet.getFeeAcctId());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getFeeAmt()),PayHfpayWallet::getFeeAmt ,payHfpayWallet.getFeeAmt());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getIdCard()),PayHfpayWallet::getIdCard ,payHfpayWallet.getIdCard());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getIdCardType()),PayHfpayWallet::getIdCardType ,payHfpayWallet.getIdCardType());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getUserName()),PayHfpayWallet::getUserName ,payHfpayWallet.getUserName());
        lqw.like(StringUtils.isNotBlank(payHfpayWallet.getUserMobile()),PayHfpayWallet::getUserMobile ,payHfpayWallet.getUserMobile());
        lqw.eq(payHfpayWallet.getUpdatedTime() != null, PayHfpayWallet::getUpdatedTime ,payHfpayWallet.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, PayHfpayWallet::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, PayHfpayWallet::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<PayHfpayWallet> list = payHfpayWalletService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取汇付通钱包信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "payHfpayWallet::getInfo", name = "获取 汇付通钱包信息 详细信息")
    public R<PayHfpayWallet> getInfo(@PathVariable("id" ) Long id) {
        return R.success(payHfpayWalletService.getById(id));
    }

    /**
     * 新增汇付通钱包信息
     */
    @PostMapping
    @Node(value = "payHfpayWallet::add", name = "新增 汇付通钱包信息")
    public R<Boolean> add(@RequestBody PayHfpayWallet payHfpayWallet) {
        return R.success(payHfpayWalletService.save(payHfpayWallet));
    }

    /**
     * 修改汇付通钱包信息
     */
    @PutMapping
    @Node(value = "payHfpayWallet::edit", name = "修改 汇付通钱包信息")
    public R<Boolean> edit(@RequestBody PayHfpayWallet payHfpayWallet) {
        return R.success(payHfpayWalletService.updateById(payHfpayWallet));
    }

    /**
     * 删除汇付通钱包信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "payHfpayWallet::remove", name = "删除 汇付通钱包信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(payHfpayWalletService.removeByIds(Arrays.asList(ids)));
    }
}