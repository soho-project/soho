package work.soho.wallet.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.user.api.service.UserApiService;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.enums.WalletTypeNameEnums;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.config.WalletSysConfig;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletLog;
import work.soho.wallet.biz.domain.WalletTransfer;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.mapper.WalletLogMapper;
import work.soho.wallet.biz.mapper.WalletTransferMapper;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletTransferService;
import work.soho.wallet.biz.service.WalletTypeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class WalletTransferServiceImpl extends ServiceImpl<WalletTransferMapper, WalletTransfer>
    implements WalletTransferService{

    private final WalletInfoApiService walletInfoApiService;
    private final WalletInfoService walletInfoService;
    private final WalletTypeService walletTypeService;
    private final UserApiService userApiService;
    private final WalletSysConfig walletSysConfig;
    private final WalletLogMapper  walletLogMapper;

    /**
     * 茸元转人民币
     *
     * @param userId
     * @param amount
     * @param remark
     * @param payPassword
     * @return
     */
    @Override
    public WalletTransfer ry2RmbTransfer(Long userId, BigDecimal amount, String remark, String payPassword) {
        // 获取用户ry钱包
        WalletType fromWalletType = walletTypeService.getByName(WalletTypeNameEnums.RY.getName());
        Assert.notNull(fromWalletType, "用户茸元钱包不存在");
        Assert.isTrue(fromWalletType.getCanWithdrawal()==1, "茸元转账暂不可用");
        WalletInfo fromWalletInfo = walletInfoService.getOne(new LambdaQueryWrapper<WalletInfo>().eq(WalletInfo::getUserId, userId).eq(WalletInfo::getType, fromWalletType.getId()));
        Assert.notNull(fromWalletInfo, "用户茸元钱包不存在");
        // 获取用户rmb钱包
        WalletType toWalletType = walletTypeService.getByName(WalletTypeNameEnums.RMB.getName());
        Assert.notNull(toWalletType, "用户人民币钱包不存在");
        WalletInfo toWalletInfo = walletInfoService.getOne(new LambdaQueryWrapper<WalletInfo>().eq(WalletInfo::getUserId, userId).eq(WalletInfo::getType, toWalletType.getId()));
        Assert.notNull(toWalletInfo, "用户人民币钱包不存在");

        // 检查支付密码是否正确
        Assert.notNull(payPassword, "支付密码不能为空");

        // TODO 支付密码检查验证

//        Boolean verifyPwd = userApiService.verificationUserInfoPayPassword(userId, payPassword);
//        Assert.isTrue(verifyPwd, "支付密码错误");

        // 茸元单日转账金额限制
        LambdaQueryWrapper<WalletTransfer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WalletTransfer::getFromUserId, userId);
        queryWrapper.eq(WalletTransfer::getFromWalletId, fromWalletInfo.getId());
        //添加当天时间区间
        queryWrapper.between(WalletTransfer::getCreatedTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0), LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        // 使用selectSum方法
        BigDecimal dailyTransferAmount = list(queryWrapper)
                .stream()
                .map(WalletTransfer::getFromAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 从钱包日志中找到钱包当日钱包金额
        LambdaQueryWrapper<WalletLog> queryWrapperAmountQuery =  new LambdaQueryWrapper<>();
        queryWrapperAmountQuery.eq(WalletLog::getWalletId, fromWalletInfo.getId());
//        queryWrapperAmountQuery.eq(WalletLog::getBizId, WalletLogEnums.BizId.WALLET_TRANSFER_OUT.getId());
        queryWrapperAmountQuery.between(WalletLog::getCreatedTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0), LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        queryWrapperAmountQuery.orderByAsc(WalletLog::getCreatedTime);
        queryWrapperAmountQuery.last("limit 1");
        WalletLog walletLog = walletLogMapper.selectOne(queryWrapperAmountQuery);
        BigDecimal dayAmount = fromWalletInfo.getAmount();
        if(walletLog != null) {
            dayAmount = walletLog.getBeforeAmount();
        }
        // 检查是否超过当日转账限额
//        if (dailyTransferAmount != null && dailyTransferAmount.add(amount).compareTo(walletSysConfig.getWalletRyTransferDayMaxRate().multiply(dayAmount)) >= 0) {
//            throw new RuntimeException("当日转账金额已达上限");
//        }

        // 计算实际到账金额
        BigDecimal commissionAmount = amount.multiply(fromWalletType.getWithdrawalCommissionRate());
        //检查最低手续费是否大于0
        if (fromWalletType.getWithdrawalMinCommission().compareTo(BigDecimal.ZERO) > 0) {
            // 如果手续费低于最低手续费，则使用最低手续费金额
            if (commissionAmount.compareTo(fromWalletType.getWithdrawalMinCommission()) < 0) {
                commissionAmount = fromWalletType.getWithdrawalMinCommission();
            }
        }

        // 计算实际转账支付金额
        BigDecimal payAmount = amount.subtract(commissionAmount);
        // 计算入账金额
        BigDecimal toAmount = payAmount.multiply(toWalletType.getRate()).divide(fromWalletType.getRate());



        // 创建转账单
        WalletTransfer walletTransfer = new WalletTransfer();
        walletTransfer.setFromWalletId(fromWalletInfo.getId());
        walletTransfer.setFromUserId(fromWalletInfo.getUserId());
        walletTransfer.setFromAmount(amount);
        walletTransfer.setFromWalletType(fromWalletInfo.getType());
        walletTransfer.setFromPayAmount(payAmount);
        walletTransfer.setFromCommissionAmount(commissionAmount);

        walletTransfer.setToAmount(toAmount);
        walletTransfer.setToWalletType(toWalletInfo.getType());
        walletTransfer.setToWalletId(toWalletInfo.getId());
        walletTransfer.setToUserId(toWalletInfo.getUserId());

        walletTransfer.setRemark(remark);
        walletTransfer.setUpdatedTime(LocalDateTime.now());
        walletTransfer.setCreatedTime(LocalDateTime.now());
        save(walletTransfer);

        // 执行转出操作
        Long logId = walletInfoApiService.changeWalletAmount(fromWalletInfo.getUserId(), fromWalletInfo.getType(), WalletLogEnums.BizId.WALLET_TRANSFER_OUT.getId()
                , walletTransfer.getCode() ,amount.negate(), "用户:"+userId+" 转账出账");

        walletInfoApiService.changeWalletAmount(toWalletInfo.getUserId(), toWalletInfo.getType(), WalletLogEnums.BizId.WALLET_TRANSFER_IN.getId(),
                walletTransfer.getCode(), walletTransfer.getToAmount(), "用户:"+userId+" 转账入账");

        return walletTransfer;
    }
}