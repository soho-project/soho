package work.soho.wallet.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletTypeDTO {
    private Integer id;
    private Integer status;
    private String name;
    private String title;
    private String notes;
    private BigDecimal withdrawalMinAmount;
    private BigDecimal withdrawalCommissionRate;
    private BigDecimal withdrawalMinCommission;
    private Integer canWithdrawal;
    private String imgUrl;
    private BigDecimal rate;
}
