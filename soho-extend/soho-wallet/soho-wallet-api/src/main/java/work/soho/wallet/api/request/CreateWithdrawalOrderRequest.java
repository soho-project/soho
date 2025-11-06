package work.soho.wallet.api.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateWithdrawalOrderRequest {
    BigDecimal amount;
    Long withdrawBankId;
    String payPassword;
    Long walletId;
    String notes;
}
