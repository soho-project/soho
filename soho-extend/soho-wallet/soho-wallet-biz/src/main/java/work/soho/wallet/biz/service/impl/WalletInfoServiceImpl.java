package work.soho.wallet.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletLog;
import work.soho.wallet.biz.enums.WalletInfoEnums;
import work.soho.wallet.biz.mapper.WalletInfoMapper;
import work.soho.wallet.biz.mapper.WalletLogMapper;
import work.soho.wallet.biz.mapper.WalletTypeMapper;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.domain.WalletType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class WalletInfoServiceImpl extends ServiceImpl<WalletInfoMapper, WalletInfo>
    implements WalletInfoService, WalletInfoApiService {

    private final WalletLogMapper walletLogMapper;
    private final WalletTypeMapper walletTypeMapper;

    @Override
    public WalletInfo getByUserIdAndType(Long userId, Integer type) {
        WalletInfo walletInfo = null;
        if (userId != null && type != null) {
            LambdaQueryWrapper<WalletInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WalletInfo::getUserId, userId);
            queryWrapper.eq(WalletInfo::getType, type);
            walletInfo = getOne(queryWrapper);
        } else {
            throw new RuntimeException("userId or type is null");
        }

        if( walletInfo == null) {
            // 检查type是否存在
            WalletType walletType = walletTypeMapper.selectById(type);
            Assert.notNull(walletType, "钱包类型不存在");

            walletInfo = new WalletInfo();
            walletInfo.setUserId(userId);
            walletInfo.setType(type);
            walletInfo.setAmount(BigDecimal.ZERO);
            walletInfo.setStatus(WalletInfoEnums.Status.ACTIVE.getId());
            walletInfo.setUpdatedTime(LocalDateTime.now());
            walletInfo.setCreatedTime(LocalDateTime.now());
            save(walletInfo);
        }
        return walletInfo;
    }

    @Override
    public WalletLog updateAmount(Long userId, Integer type, BigDecimal amount, String notes) {
        WalletInfo walletInfo = getByUserIdAndType(userId, type);
        return updateAmount(walletInfo, amount, notes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletLog updateAmount(WalletInfo walletInfo, BigDecimal amount, String notes) {
        return updateAmountWithBiz(walletInfo, amount, notes, null, null);
    }


    @Transactional(rollbackFor = Exception.class)
    public WalletLog updateAmountWithBiz(WalletInfo walletInfo, BigDecimal amount, String notes, Integer bizId, String outTrackId) {
        if (walletInfo != null) {
            BigDecimal computeAmount = walletInfo.getAmount().add(amount);
            Assert.isTrue(computeAmount.compareTo(BigDecimal.ZERO) >= 0, "余额不足");

            // 添加钱包日志
            WalletLog walletLog = new WalletLog();
            walletLog.setWalletId(walletInfo.getId());
            walletLog.setAmount(amount);
            walletLog.setBeforeAmount(walletInfo.getAmount());
            walletLog.setAfterAmount(computeAmount);
            walletLog.setNotes(notes);
            walletLog.setBizId(bizId);
            walletLog.setOutTrackId(outTrackId);
            walletLog.setCreatedTime(LocalDateTime.now());
            walletLogMapper.insert(walletLog);

            walletInfo.setAmount(computeAmount);
            walletInfo.setUpdatedTime(LocalDateTime.now());
            updateById(walletInfo);

            return walletLog;
        }
        return null;
    }

    /**
     * 外部钱包调用
     *
     * @param userId
     * @param type
     * @param bizId
     * @param outTrackId
     * @param amount
     * @param notes
     * @return
     */
    @Override
    public Long changeWalletAmount(Long userId, Integer type, Integer bizId, String outTrackId, BigDecimal amount, String notes) {
        WalletInfo walletInfo = getByUserIdAndType(userId, type);
        return updateAmountWithBiz(walletInfo, amount, notes, bizId, outTrackId).getId();
    }

    @Override
    public Boolean isExists(Long userId, Integer type, Integer bizId, String outTrackId) {
        // 获取钱包信息
        WalletInfo walletInfo = getByUserIdAndType(userId, type);
        LambdaQueryWrapper<WalletLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WalletLog::getWalletId, walletInfo.getId());
        queryWrapper.eq(WalletLog::getBizId, bizId);
        queryWrapper.eq(WalletLog::getOutTrackId, outTrackId);
        return walletLogMapper.exists(queryWrapper);
    }
}