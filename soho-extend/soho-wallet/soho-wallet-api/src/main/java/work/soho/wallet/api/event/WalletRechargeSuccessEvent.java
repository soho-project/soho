package work.soho.wallet.api.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包充值成功事件
 */
@Data
public class WalletRechargeSuccessEvent {
    @ApiModelProperty(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @ApiModelProperty(value = "wallet_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long walletId;

    @ApiModelProperty(value = "pay_id")
    private Integer payId;

    @ApiModelProperty(value = "status")
    private Integer status;

    @ApiModelProperty(value = "transaction_no")
    private String transactionNo;

    @ApiModelProperty(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @ApiModelProperty(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
