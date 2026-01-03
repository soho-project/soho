package work.soho.express.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class OrderItem {
    // 物品名称
    private String name;
    // 数量
    private Integer qty;
    // 重量
    private BigDecimal weight;
    // 备注
    private String remark;
}
