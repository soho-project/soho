package work.soho.wallet.api.service;

import java.math.BigDecimal;

public interface WalletInfoApiService {
    Long changeWalletAmount(Long userId, Integer type, Integer bizId, String outTrackId, BigDecimal amount, String notes);

    // 检查支付单是否存在
    Boolean isExists(Long userId, Integer type, Integer bizId, String outTrackId);
}
