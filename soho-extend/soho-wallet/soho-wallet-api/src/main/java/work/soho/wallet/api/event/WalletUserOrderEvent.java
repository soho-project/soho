package work.soho.wallet.api.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户支付单事件
 */
@Data
public class WalletUserOrderEvent {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * wallet_type
     */
    @ApiModelProperty(value = "wallet_type")
    private Integer walletType;

    /**
     * wallet_id
     */
    @ApiModelProperty(value = "wallet_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer walletId;

    /**
     * no
     */
    @ApiModelProperty(value = "no")
    private String no;

    /**
     * amount
     */
    @ApiModelProperty(value = "amount")
    private BigDecimal amount;

    /**
     * out_tracking_number
     */
    @ApiModelProperty(value = "out_tracking_number")
    private String outTrackingNumber;

    /**
     * status
     */
    @ApiModelProperty(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
     * notes
     */
    @ApiModelProperty(value = "notes")
    private String notes;

    /**
     * updated_time
     */
    @ApiModelProperty(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * created_time
     */
    @ApiModelProperty(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Getter
    @RequiredArgsConstructor
    public static enum Status {
        PENDING_PAYMENT(0,"待支付"),
        PAYMENT_FAILED(40,"支付失败"),
        CANCELED(30,"已取消"),
        PAYMENT_SUCCESSFUL(20,"支付成功"),
        PROCESSING_PAYMENT(10,"支付中");
        private final int id;
        private final String name;
    }
}
