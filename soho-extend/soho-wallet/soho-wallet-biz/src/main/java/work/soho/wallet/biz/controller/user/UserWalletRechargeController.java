package work.soho.wallet.biz.controller.user;

import java.time.LocalDateTime;

import cn.hutool.core.lang.Assert;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.security.userdetails.SohoUserDetails;import work.soho.common.core.util.PageUtils;
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
import work.soho.common.security.utils.SecurityUtils;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.wallet.biz.domain.WalletRecharge;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletRechargeService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.wallet.biz.enums.WalletRechargeEnums;
import work.soho.pay.api.service.PayOrderApiService;
import work.soho.pay.api.dto.OrderDetailsDto;
import work.soho.wallet.biz.service.WalletTypeService;
import work.soho.wallet.biz.domain.*;
import work.soho.wallet.api.enums.WalletTypeNameEnums;

/**
 * 钱包充值Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletRecharge" )
public class UserWalletRechargeController {

    private final WalletRechargeService walletRechargeService;
    private final WalletInfoService walletInfoService;
    private final WalletTypeService walletTypeService;
    private final PayOrderApiService payOrderApiService;

    /**
     * 查询钱包充值列表
     */
    @GetMapping("/list")
    public R<PageSerializable<WalletRecharge>> list(WalletRecharge walletRecharge, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletRecharge> lqw = new LambdaQueryWrapper<WalletRecharge>();
        lqw.eq(walletRecharge.getId() != null, WalletRecharge::getId ,walletRecharge.getId());
        lqw.like(StringUtils.isNotBlank(walletRecharge.getCode()),WalletRecharge::getCode ,walletRecharge.getCode());
        lqw.eq(walletRecharge.getAmount() != null, WalletRecharge::getAmount ,walletRecharge.getAmount());
        lqw.eq(WalletRecharge::getUserId ,sohoUserDetails.getId());
        lqw.eq(walletRecharge.getWalletId() != null, WalletRecharge::getWalletId ,walletRecharge.getWalletId());
        lqw.eq(walletRecharge.getStatus() != null, WalletRecharge::getStatus ,walletRecharge.getStatus());
        lqw.eq(walletRecharge.getUpdatedTime() != null, WalletRecharge::getUpdatedTime ,walletRecharge.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletRecharge::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletRecharge::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletRecharge> list = walletRechargeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包充值详细信息
     */
    @GetMapping(value = "/{id}" )
    public R<WalletRecharge> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<WalletRecharge> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletRecharge::getId, id);
        lqw.eq(WalletRecharge::getUserId ,sohoUserDetails.getId());
        WalletRecharge walletRecharge = walletRechargeService.getOne(lqw);
        return R.success(walletRecharge);
    }

    /**
     * 新增钱包充值
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public R<Map<String, String>> add(@RequestBody WalletRecharge walletRecharge, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        walletRecharge.setUserId(sohoUserDetails.getId());

        //目前只支持rmb钱包充值， 检查钱包类型是否为 rmb 类型
        LambdaQueryWrapper<WalletType> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletType::getName, WalletTypeNameEnums.RMB.getName());
        WalletType walletType = walletTypeService.getOne(lqw);
        Assert.notNull(walletType, "钱包类型不存在");

        WalletInfo info = walletInfoService.getByUserIdAndType(sohoUserDetails.getId(), walletType.getId());
        Assert.notNull(info, "钱包不存在");

        walletRecharge.setCode(IDGeneratorUtils.snowflake().toString());
        walletRecharge.setStatus(WalletRechargeEnums.Status.TO_BE_RECHARGED.getId());
        walletRecharge.setWalletId(info.getId());
        if(!walletRechargeService.save(walletRecharge)) {
            return R.error("充值失败");
        }

        Map<String, String> map = payOrderApiService.payOrder(OrderDetailsDto.builder()
                .userId(sohoUserDetails.getId())
                .payInfoId(walletRecharge.getPayId()) // TODO 调用后台配置地址
                .amount(walletRecharge.getAmount())
                .outTradeNo(walletRecharge.getCode())
                .description("支付单："+ walletRecharge.getCode())
                .build());

        // 判断支付预调接口是否成功
        if(!"C00000".equals(map.get("resp_code"))) {
            // 这里支付失败了， 需要进行额外的处理
            throw new RuntimeException("支付遇到问题， 请检查是否开通创建支付钱包！");
        }

        return R.success(map);
    }

}