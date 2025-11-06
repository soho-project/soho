package work.soho.pay.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.pay.api.dto.CreateWalletDto;
import work.soho.pay.api.service.PayHfpayWalletApiService;
import work.soho.pay.api.vo.HftPayCreateWallet;
import work.soho.pay.biz.domain.PayHfpayWallet;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.enums.PayHfpayWalletEnums;
import work.soho.pay.biz.mapper.PayHfpayWalletMapper;
import work.soho.pay.biz.platform.FactoryApis;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.payapis.HftCreateWallet;
import work.soho.pay.biz.service.PayHfpayWalletService;
import work.soho.pay.biz.service.PayInfoService;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class PayHfpayWalletServiceImpl extends ServiceImpl<PayHfpayWalletMapper, PayHfpayWallet>
    implements PayHfpayWalletService, PayHfpayWalletApiService {

    private final PayInfoService payInfoService;

    @Override
    public HftPayCreateWallet createWallet(CreateWalletDto createWalletDto) {
        // 检查是否已经存在钱包
        PayHfpayWallet wallet = getActiveOne(createWalletDto.getPayInfoId(), createWalletDto.getUserId());
        if (wallet != null) {
            HftPayCreateWallet hftPayCreateWallet = new HftPayCreateWallet();
            hftPayCreateWallet.setUserCustId(wallet.getUserCustId());
            return hftPayCreateWallet;
        }

        //获取支付方案
        PayInfo payInfo = payInfoService.getById(createWalletDto.getPayInfoId());
        PayConfig payConfig = new PayConfig();
        payConfig.setId(payInfo.getId());
        payConfig.setPayCertificate(payInfo.getAccountPublicKey());
        payConfig.setMerchantSerialNumber(payInfo.getAccountSerialNumber());
        payConfig.setPrivateKey(payInfo.getAccountPrivateKey());
        payConfig.setMerchantId(payInfo.getAccountId());
        payConfig.setAppId(payInfo.getAccountAppId());
        //获取支付驱动
        HftCreateWallet payApis = (HftCreateWallet)FactoryApis.getHftCreateWalletApisByName(payInfo.getAdapterName(), payConfig);

        return payApis.createWallet(createWalletDto);
    }

    @Override
    public HashMap<String, String> queryWallet(Long userId) {
        //获取支付方案
        PayHfpayWallet wallet = getOne(new LambdaQueryWrapper<PayHfpayWallet>().eq(PayHfpayWallet::getUserId, userId).eq(PayHfpayWallet::getStatus, PayHfpayWalletEnums.Status.SUCCESS.getCode()));
        if (wallet == null) {
            return null;
        }
        //获取支付驱动
        PayInfo payInfo = payInfoService.getById(wallet.getPayInfoId());
        PayConfig payConfig = new PayConfig();
        payConfig.setId(payInfo.getId());
        payConfig.setPayCertificate(payInfo.getAccountPublicKey());
        payConfig.setMerchantSerialNumber(payInfo.getAccountSerialNumber());
        payConfig.setPrivateKey(payInfo.getAccountPrivateKey());
        payConfig.setMerchantId(payInfo.getAccountId());
        payConfig.setAppId(payInfo.getAccountAppId());

        HftCreateWallet payApis = (HftCreateWallet)FactoryApis.getHftCreateWalletApisByName(payInfo.getAdapterName(), payConfig);

        return payApis.queryWallet(wallet.getUserCustId());
    }

    /**
     * 获取指定用户的钱包地址
     *
     * @param userId
     * @return
     */
    @Override
    public HashMap<String, String> queryWalletAddress(Long userId) {
        //获取支付方案
        PayHfpayWallet wallet = getOne(new LambdaQueryWrapper<PayHfpayWallet>().eq(PayHfpayWallet::getUserId, userId).eq(PayHfpayWallet::getStatus, PayHfpayWalletEnums.Status.SUCCESS.getCode()));
        return queryWalletAddress(wallet);
    }

    @Override
    public HashMap<String, String> queryWalletAddress(PayHfpayWallet wallet) {
        if (wallet == null) {
            return null;
        }
        //获取支付驱动
        PayInfo payInfo = payInfoService.getById(wallet.getPayInfoId());
        PayConfig payConfig = new PayConfig();
        payConfig.setId(payInfo.getId());
        payConfig.setPayCertificate(payInfo.getAccountPublicKey());
        payConfig.setMerchantSerialNumber(payInfo.getAccountSerialNumber());
        payConfig.setPrivateKey(payInfo.getAccountPrivateKey());
        payConfig.setMerchantId(payInfo.getAccountId());
        payConfig.setAppId(payInfo.getAccountAppId());

        HftCreateWallet payApis = (HftCreateWallet)FactoryApis.getHftCreateWalletApisByName(payInfo.getAdapterName(), payConfig);

        return payApis.queryWalletAddress(wallet.getUserCustId());
    }

    /**
     * 获取钱包余额
     *
     * @param userId
     * @return
     */
    @Override
    public HashMap<String, String> queryWalletBalance(Long userId) {
        //获取支付方案
        PayHfpayWallet wallet = getOne(new LambdaQueryWrapper<PayHfpayWallet>()
                .eq(PayHfpayWallet::getUserId, userId)
                .eq(PayHfpayWallet::getStatus, PayHfpayWalletEnums.Status.SUCCESS.getCode()));
        if (wallet == null) {
            return null;
        }

        return queryWalletBalanceWithPayInfoId(wallet.getPayInfoId(), userId);
    }

    /**
     * 获取钱包余额
     *
     * @param userId
     * @return
     */
    @Override
    public HashMap<String, String> queryWalletBalanceWithPayInfoId(Integer payInfoId, Long userId) {
        //获取支付方案
        PayHfpayWallet wallet = getOne(new LambdaQueryWrapper<PayHfpayWallet>().eq(PayHfpayWallet::getUserId, userId)
                .eq(PayHfpayWallet::getPayInfoId, payInfoId)
                .eq(PayHfpayWallet::getStatus, PayHfpayWalletEnums.Status.SUCCESS.getCode()));
        if (wallet == null) {
            return null;
        }
        //获取支付驱动
        PayInfo payInfo = payInfoService.getById(wallet.getPayInfoId());
        PayConfig payConfig = new PayConfig();
        payConfig.setId(payInfo.getId());
        payConfig.setPayCertificate(payInfo.getAccountPublicKey());
        payConfig.setMerchantSerialNumber(payInfo.getAccountSerialNumber());
        payConfig.setPrivateKey(payInfo.getAccountPrivateKey());
        payConfig.setMerchantId(payInfo.getAccountId());
        payConfig.setAppId(payInfo.getAccountAppId());

        HftCreateWallet payApis = (HftCreateWallet)FactoryApis.getHftCreateWalletApisByName(payInfo.getAdapterName(), payConfig);

        return payApis.queryWalletBalance(wallet.getUserCustId(), wallet.getAcctId());
    }


    public PayHfpayWallet getActiveOne(Integer payInfoId, Long userId) {
        LambdaQueryWrapper<PayHfpayWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayHfpayWallet::getPayInfoId, payInfoId);
        queryWrapper.eq(PayHfpayWallet::getUserId, userId);
        queryWrapper.eq(PayHfpayWallet::getStatus, PayHfpayWalletEnums.Status.SUCCESS.getCode());
        return getBaseMapper().selectOne(queryWrapper);
    }
}