package work.soho.wallet.api.service;

import work.soho.wallet.api.dto.WalletTypeDTO;

public interface WalletTypeApiService {

    WalletTypeDTO getWalletTypeById(Integer walletTypeId);
}
