package work.soho.pay.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.pay.biz.domain.PayHfpayWallet;

import java.util.HashMap;

public interface PayHfpayWalletService extends IService<PayHfpayWallet> {
    HashMap<String, String> queryWallet(Long userId);
    HashMap<String, String> queryWalletAddress(Long userId);
    HashMap<String, String> queryWalletAddress(PayHfpayWallet wallet);
    HashMap<String, String> queryWalletBalance(Long userId);
    HashMap<String, String> queryWalletBalanceWithPayInfoId(Integer payInfoId, Long userId);
}