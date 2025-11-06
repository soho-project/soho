package work.soho.wallet.biz.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class WalletWithdrawalOrderAliBankVo {

    private Long id;

    /**
     * 持卡人
     */
    @ExcelProperty("收款方名称")
    @ApiModelProperty(value = "收款方名称")
    private String cardName;

    /**
     * 银行卡号
     */
    @ExcelProperty("收款方账号")
    @ApiModelProperty(value = "收款方账号")
    private String cardCode;

    /**
     * 银行名称
     */
    @ExcelProperty("收款方开户行名称 ")
    @ApiModelProperty(value = "收款方开户行名称 ")
    private String bankName = "";

    /**
     * 联行号
     */
    @ExcelProperty("收款行联行号")
    @ApiModelProperty(value = "收款行联行号")
    private String interbankNumber = "";

    /**
     * 金额
     */
    @ExcelProperty("金额")
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    /**
     * 附言/用途
     */
    @ExcelProperty("附言/用途")
    @ApiModelProperty(value = "附言/用途")
    private String purpose;
}
