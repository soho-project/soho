package work.soho.pay.api.dto;

import lombok.Data;
import work.soho.pay.api.vo.PayOrderVo;

import java.util.Map;

/**
 * 预支付请求参数
 */
@Data
public class CreatePayInfoDto {
    /**
     * 支付单信息
     */
    private PayOrderVo payOrder;

    /**
     * 支付参数
     */
    private Map<String, String> payParams;
}
