package work.soho.wallet.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletTransfer;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.enums.WalletTypeEnums;
import work.soho.wallet.biz.mapper.WalletInfoMapper;
import work.soho.wallet.biz.mapper.WalletTransferMapper;
import work.soho.wallet.biz.service.WalletTransferService;
import work.soho.wallet.biz.service.WalletTypeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WalletTransferServiceImpl extends ServiceImpl<WalletTransferMapper, WalletTransfer>
    implements WalletTransferService{

    private final WalletInfoApiService walletInfoApiService;
    private final WalletTypeService walletTypeService;
    private final WalletInfoMapper walletInfoMapper;

    /**
     * 转账接口
     *
     * @param userId
     * @param walletId
     * @param toWalletId
     * @param amount
     * @param remark
     * @return
     */
    @Override
    public WalletTransfer transfer(Long userId, Long walletId, Long toWalletId, BigDecimal amount, String remark) {
        WalletInfo fromWalletInfo = walletInfoMapper.selectById(walletId);
        Assert.notNull(fromWalletInfo, "用户钱包不存在");
        Assert.isTrue(fromWalletInfo.getStatus()==1, "用户钱包不存在");
        WalletInfo toWalletInfo = walletInfoMapper.selectById(toWalletId);
        Assert.notNull(toWalletInfo, "用户钱包不存在");
        Assert.isTrue(toWalletInfo.getStatus()==1, "用户钱包不存在");
        Assert.isTrue(fromWalletInfo.getUserId().equals(toWalletInfo.getUserId()), "用户钱包不存在");


        // 获取转出钱包类型
        WalletType fromWalletType = walletTypeService.getById(fromWalletInfo.getType());
        Assert.notNull(fromWalletType, "用户钱包异常");
        Assert.isTrue(fromWalletType.getCanTransferOut()== WalletTypeEnums.CanTransferOut.YES.getId(), "该钱包转账暂不可用");
        // 获取用户rmb钱包
        WalletType toWalletType = walletTypeService.getById(toWalletInfo.getType());
        Assert.notNull(toWalletType, "目标钱包类型异常");

        // 检查转入钱包是否支持来源类型钱包
        if(toWalletType.getCanTransferInTypes() == null) {
            Assert.isTrue(toWalletType.getCanTransferInTypes().contains(fromWalletInfo.getType().toString()), "目标钱包不支持来源钱包类型");
        } else {
            List<String> canTransferInTypes = Arrays.asList(toWalletType.getCanTransferInTypes().split( ","));
            if(!canTransferInTypes.contains(fromWalletInfo.getType().toString())) {
                Assert.isTrue(false, "目标钱包不支持来源钱包类型");
            }
        }

        // 转账金额限制
//        LambdaQueryWrapper<WalletTransfer> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(WalletTransfer::getFromUserId, userId);
//        queryWrapper.eq(WalletTransfer::getFromWalletId, fromWalletInfo.getId());

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
        walletTransfer.setCode(IDGeneratorUtils.snowflake().toString());
        save(walletTransfer);

        // 执行转出操作
        Long logId = walletInfoApiService.changeWalletAmount(fromWalletInfo.getUserId(), fromWalletInfo.getType(), WalletLogEnums.BizId.WALLET_TRANSFER_OUT.getId()
                , walletTransfer.getCode() ,amount.negate(), "用户:"+userId+" 转账出账");

        walletInfoApiService.changeWalletAmount(toWalletInfo.getUserId(), toWalletInfo.getType(), WalletLogEnums.BizId.WALLET_TRANSFER_IN.getId(),
                walletTransfer.getCode(), walletTransfer.getToAmount(), "用户:"+userId+" 转账入账");

        return walletTransfer;
    }
}