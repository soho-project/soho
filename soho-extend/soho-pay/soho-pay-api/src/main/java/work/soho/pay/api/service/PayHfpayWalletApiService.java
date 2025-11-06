package work.soho.pay.api.service;

import work.soho.pay.api.dto.CreateWalletDto;
import work.soho.pay.api.vo.HftPayCreateWallet;

public interface PayHfpayWalletApiService {
    HftPayCreateWallet createWallet(CreateWalletDto createWalletDto);
}