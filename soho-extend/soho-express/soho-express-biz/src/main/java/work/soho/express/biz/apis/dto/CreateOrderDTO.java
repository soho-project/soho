package work.soho.express.biz.apis.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateOrderDTO {
    /**
     * 快递公司订单号
     */
    private String orderNo;

    /**
     * 快递公司运单号
     */
    private String billCode;
}
