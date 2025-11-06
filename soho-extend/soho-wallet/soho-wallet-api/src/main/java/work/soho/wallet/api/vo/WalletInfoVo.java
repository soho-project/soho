package work.soho.wallet.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletInfoVo {
    private Long id;
    private Integer type;
    private String typeName;
    private String typeTitle;
    private String typeDesc;
    private BigDecimal amount;
}
