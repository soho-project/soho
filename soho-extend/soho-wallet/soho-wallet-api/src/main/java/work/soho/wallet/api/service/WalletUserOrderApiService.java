package work.soho.wallet.api.service;

import work.soho.wallet.api.dto.CreateUserOrderDTO;

public interface WalletUserOrderApiService {
    CreateUserOrderDTO createPayOrder(CreateUserOrderDTO createUserOrderDTO);
}
