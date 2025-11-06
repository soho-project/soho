package work.soho.wallet.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.api.vo.WalletInfoVo;
import work.soho.wallet.api.vo.WalletTransferVo;
import work.soho.wallet.biz.config.WalletSysConfig;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletTransfer;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.enums.WalletInfoEnums;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletTransferService;
import work.soho.wallet.biz.service.WalletTypeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletInfo" )
public class UserWalletInfoController {

    private final WalletInfoService walletInfoService;
    private final WalletInfoApiService walletInfoApiService;
    private final WalletTypeService walletTypeService;
    private final UserApiService userApiService;
    private final WalletSysConfig walletSysConfig;
    private final WalletTransferService walletTransferService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    public R<PageSerializable<WalletInfoVo>> list(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, WalletInfo walletInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletInfo> lqw = new LambdaQueryWrapper<WalletInfo>();
        lqw.eq(WalletInfo::getUserId ,sohoUserDetails.getId());
        lqw.eq(walletInfo.getId() != null, WalletInfo::getId ,walletInfo.getId());
        lqw.eq(walletInfo.getType() != null, WalletInfo::getType ,walletInfo.getType());
        lqw.eq(walletInfo.getAmount() != null, WalletInfo::getAmount ,walletInfo.getAmount());
        lqw.eq(walletInfo.getStatus() != null, WalletInfo::getStatus ,walletInfo.getStatus());
        lqw.eq(walletInfo.getUpdatedTime() != null, WalletInfo::getUpdatedTime ,walletInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletInfo> list = walletInfoService.list(lqw);

        List<Integer> typeIds = list.stream().map(item -> item.getType()).collect(Collectors.toList());
        List<WalletType> walletTypes = walletTypeService.listByIds(typeIds);
        Map<Integer, WalletType> mapWalletTypes = walletTypes.stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
        List<WalletInfoVo> voList = list.stream().map(item -> {
            WalletInfoVo vo = BeanUtils.copy(item, WalletInfoVo.class);
            WalletType walletType = mapWalletTypes.get(item.getType());
            vo.setTypeName(walletType.getName());
            vo.setTypeTitle(walletType.getTitle());
            vo.setTypeDesc(walletType.getNotes());
            return vo;
        }).collect(Collectors.toList());

        return R.success(new PageSerializable<>(voList));
    }

    /**
     * 获取用户可用的钱包列表
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/getUserWalletList")
    public R<List<WalletInfoVo>> list(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        List<WalletType> walletTypes = walletTypeService.list(new LambdaQueryWrapper<WalletType>().eq(WalletType::getStatus, WalletInfoEnums.Status.ACTIVE.getId()));
        // 确保账号已经创建
        walletTypes.forEach(item -> {
            walletInfoService.getByUserIdAndType(sohoUserDetails.getId(), item.getId());
        });

        LambdaQueryWrapper<WalletInfo> lqw = new LambdaQueryWrapper<WalletInfo>();
        lqw.eq(WalletInfo::getUserId, sohoUserDetails.getId());
        lqw.eq(WalletInfo::getStatus, WalletInfoEnums.Status.ACTIVE.getId());
        List<WalletInfo> list = walletInfoService.list(lqw);


        Map<Integer, WalletType> mapWalletTypes = walletTypes.stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
        List<WalletInfoVo> voList = list.stream().map(item -> {
            WalletInfoVo vo = BeanUtils.copy(item, WalletInfoVo.class);
            WalletType walletType = mapWalletTypes.get(item.getType());
            vo.setTypeName(walletType.getName());
            vo.setTypeTitle(walletType.getTitle());
            vo.setTypeDesc(walletType.getNotes());
            return vo;
        }).collect(Collectors.toList());
        return R.success(voList);
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{type}")
    public R<WalletInfo> getInfo(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @PathVariable Integer type) {
        return R.success(walletInfoService.getByUserIdAndType(sohoUserDetails.getId(), type));
    }

    /**
     * 新增
     */
    @PostMapping
    public R<Boolean> add(@RequestBody WalletInfo walletInfo) {
        return R.success(walletInfoService.save(walletInfo));
    }

    /**
     * 修改
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody WalletInfo walletInfo) {
        return R.success(walletInfoService.updateById(walletInfo));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(walletInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 转账接口
     *
     * @param sohoUserDetails
     * @param walletTransferVo
     * @return
     */
    @Transactional
    @PostMapping("/transfer")
    public R<Long> transfer(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @RequestBody WalletTransferVo walletTransferVo) {
        // 获取转出钱包，并且验证是否为当前用户
        WalletInfo fromWalletInfo = walletInfoService.getById(walletTransferVo.getFromWalletId());
        Assert.notNull(fromWalletInfo, "转出钱包不存在");
        Assert.isTrue(fromWalletInfo.getUserId().equals(sohoUserDetails.getId()), "转出钱包不存在， 请检查");
        Assert.isTrue(fromWalletInfo.getStatus() != WalletInfoEnums.Status.DISABLE.getId(), "转出钱包被禁用");

        // 获取转出钱包类型
        WalletType fromWalletType = walletTypeService.getById(fromWalletInfo.getType());
        Assert.isTrue(fromWalletType.getStatus() != WalletInfoEnums.Status.DISABLE.getId(), "钱包类型被禁用");

        // 获取转入钱包ID
        Long toWalletId = walletTransferVo.getToWalletId();
        if(toWalletId == null) {
            Assert.notNull(walletTransferVo.getToPhone(), "请传递收账用户手机号， 请检查");
            UserInfoDto toUserInfo = userApiService.getUserInfoByPhone(walletTransferVo.getToPhone());
            WalletInfo searchToWalletInfo = walletInfoService.getByUserIdAndType(toUserInfo.getId(), fromWalletType.getId());
            Assert.notNull(searchToWalletInfo, "收账用户钱包不存在");
            toWalletId = searchToWalletInfo.getId();
        }

        // 获取转入钱包
        WalletInfo toWalletInfo = walletInfoService.getById(toWalletId);
        Assert.notNull(toWalletInfo, "转入钱包不存在");
        Assert.isTrue(toWalletInfo.getStatus() != WalletInfoEnums.Status.DISABLE.getId(), "转入钱包被禁用");
        // 获取转入钱包类型
        WalletType toWalletType = walletTypeService.getById(toWalletInfo.getType());
        Assert.isTrue(toWalletType.getStatus() != WalletInfoEnums.Status.DISABLE.getId(), "钱包类型被禁用");
        // 判断转出，转入钱包类型是否一致
        Assert.isTrue(toWalletType.getId().equals(fromWalletType.getId()), "转入钱包不支持转账");

        // 检查支付密码是否正确
        Boolean verifyPwd = userApiService.verificationUserInfoPayPassword(sohoUserDetails.getId(), walletTransferVo.getPayPassword());
        Assert.isTrue(verifyPwd, "支付密码错误");

        // 查询用户当日茸元转账金额


        // 计算入账金额
        BigDecimal toAmount = walletTransferVo.getAmount().multiply(toWalletType.getRate()).divide(fromWalletType.getRate());

        // 创建转账单
        WalletTransfer walletTransfer = new WalletTransfer();
        walletTransfer.setCode(IDGeneratorUtils.snowflake().toString());
        walletTransfer.setFromWalletId(walletTransferVo.getFromWalletId());
        walletTransfer.setFromUserId(fromWalletInfo.getUserId());
        walletTransfer.setFromAmount(walletTransferVo.getAmount());
        walletTransfer.setFromWalletType(fromWalletInfo.getType());

        walletTransfer.setToAmount(toAmount);
        walletTransfer.setToWalletType(toWalletInfo.getType());
        walletTransfer.setToWalletId(toWalletInfo.getId());
        walletTransfer.setToUserId(toWalletInfo.getUserId());

        walletTransfer.setRemark(walletTransferVo.getRemark());
        walletTransfer.setUpdatedTime(LocalDateTime.now());
        walletTransfer.setCreatedTime(LocalDateTime.now());
        walletTransferService.save(walletTransfer);

        // 执行转出操作
        Long logId = walletInfoApiService.changeWalletAmount(fromWalletInfo.getUserId(), fromWalletInfo.getType(), WalletLogEnums.BizId.WALLET_TRANSFER_OUT.getId()
                , "F"+walletTransfer.getCode() ,walletTransferVo.getAmount().negate(), "用户:"+sohoUserDetails.getId()+" 转账出账");

        walletInfoApiService.changeWalletAmount(toWalletInfo.getUserId(), toWalletInfo.getType(), WalletLogEnums.BizId.WALLET_TRANSFER_IN.getId(),
                "T"+walletTransfer.getCode(), toAmount, "用户:"+sohoUserDetails.getId()+" 转账入账");

        return R.success(logId);
    }

}