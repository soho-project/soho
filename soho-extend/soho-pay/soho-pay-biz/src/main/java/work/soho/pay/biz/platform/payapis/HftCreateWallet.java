package work.soho.pay.biz.platform.payapis;

import work.soho.pay.api.dto.CreateWalletDto;
import work.soho.pay.api.vo.HftPayCreateWallet;

import java.util.HashMap;

public interface HftCreateWallet {
    HftPayCreateWallet createWallet(CreateWalletDto createWalletDto);
    HashMap<String, String> queryWallet(String userCustId);
    HashMap<String, String> queryWalletAddress(String userCustId);
    HashMap<String, String> queryWalletBalance(String userCustId, String acctId);
}
