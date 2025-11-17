package work.soho.wallet.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CreateUserOrderDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "wallet_id")
    private Integer walletId;

    /**
     * 钱包类型名称
     */
    @ApiModelProperty(value = "wallet_type_name")
    private String walletTypeName;

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
}
