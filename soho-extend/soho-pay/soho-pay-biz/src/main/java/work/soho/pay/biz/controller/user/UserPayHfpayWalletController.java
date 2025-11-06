package work.soho.pay.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.pay.api.dto.CreateWalletDto;
import work.soho.pay.api.service.PayHfpayWalletApiService;
import work.soho.pay.api.vo.HftPayCreateWallet;
import work.soho.pay.biz.domain.PayHfpayWallet;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.enums.PayHfpayWalletEnums;
import work.soho.pay.biz.service.PayHfpayWalletService;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.user.api.dto.UserCertificationDto;
import work.soho.user.api.service.UserCertificationApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Api(tags = "汇付通钱包信息")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay/user/payHfpayWallet" )
public class UserPayHfpayWalletController {
    private final PayHfpayWalletApiService userPayHfpayWalletService;
    private final PayHfpayWalletService payHfpayWalletService;
    private final UserCertificationApiService userCertificationApiService;
    private final PayInfoService payInfoService;

    /**
     * 获取创建汇付通钱包信息
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/wallet")
    public R<HftPayCreateWallet> getWallet(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        // 获取用户实名信息，获取身份证号码用来创建三方钱包
        UserCertificationDto userCertificationDto = userCertificationApiService.getActiveUserCertificationByUserId(sohoUserDetails.getId());
        if(userCertificationDto == null) {
            return R.error("请先完成实名认证,再开通支付钱包");
        }

        // 获取当前第一个汇付通账号创建钱包
        LambdaQueryWrapper<PayInfo> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(PayInfo::getAdapterName, "adapay_h5");
        lambdaQuery.eq(PayInfo::getStatus, 1);
        lambdaQuery.orderByAsc(PayInfo::getId);
        lambdaQuery.last("limit 1");
        PayInfo payInfo = payInfoService.getOne(lambdaQuery);
        if(payInfo == null) {
            return R.error("请先配置汇付通支付信息");
        }

        CreateWalletDto createWalletDto = new CreateWalletDto();
        createWalletDto.setIdCard(userCertificationDto.getCardId());
        createWalletDto.setUserName(userCertificationDto.getName());
        createWalletDto.setUserId(sohoUserDetails.getId());
        createWalletDto.setPayInfoId(payInfo.getId());
        HftPayCreateWallet wallet = userPayHfpayWalletService.createWallet(createWalletDto);
        return R.success(wallet);
    }

    /**
     * 获取钱包地址
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/wallet-address/{id}")
    public R<HashMap<String,String>> getWalletAddress(@PathVariable("id") Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        // 获取用户钱包地址
        PayHfpayWallet payHfpayWallet = payHfpayWalletService.getById(id);
        //检查钱包是否为当前用户钱包
        if(!payHfpayWallet.getUserId().equals(sohoUserDetails.getId())) {
            return R.error("钱包不存在， 请检查");
        }

        //检查该钱包是否为当前用户的
        if(!payHfpayWallet.getUserId().equals(sohoUserDetails.getId())) {
            return R.error("钱包不存在， 请检查");
        }
        if(payHfpayWallet.getStatus() != PayHfpayWalletEnums.Status.SUCCESS.getCode()) {
            return R.error("钱包状态异常，请稍后重试");
        }

        return R.success(payHfpayWalletService.queryWalletAddress(payHfpayWallet));
    }

    /**
     * 获取用户钱包列表信息
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/list")
    public R<List<HashMap<String, String>>> list(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        // 查询汇付通支付方式列表
        List<PayInfo> payInfoList = payInfoService.list(new LambdaQueryWrapper<PayInfo>().eq(PayInfo::getAdapterName, "adapay_h5").eq(PayInfo::getStatus, 1));
        List<HashMap<String, String>> res = new ArrayList<>();
        payInfoList.forEach(payInfo -> {
            // 获取钱包信息
            PayHfpayWallet payHfpayWallet = payHfpayWalletService.getOne(new LambdaQueryWrapper<PayHfpayWallet>()
                    .eq(PayHfpayWallet::getPayInfoId, payInfo.getId()).eq(PayHfpayWallet::getUserId, sohoUserDetails.getId()));
            log.info("payHfpayWallet:{}", payHfpayWallet);
            log.info(sohoUserDetails.getId());
            log.info(payInfo.getId());
            if(payHfpayWallet != null) {
                HashMap<String, String> wallet = payHfpayWalletService.queryWalletBalanceWithPayInfoId(payInfo.getId(), sohoUserDetails.getId());
                wallet.put("pay_info_id", payInfo.getId().toString());
                wallet.put("title", payInfo.getTitle());
                wallet.put("id", payHfpayWallet.getId().toString());
                res.add(wallet);
            }
        });

        return R.success(res);
    }
}
