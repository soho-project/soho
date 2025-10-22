package work.soho.shop.api.request;

import lombok.Data;
import work.soho.shop.api.vo.ProductSkuVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建请求
 *
 */
@Data
public class OrderCreateRequest {
    /**
     * 用户ID
     *
     * 服务器端填充
     */
    private Integer userId;

    /**
     * 用户地址ID
     */
    private Integer userAddressId;

    /**
     * 优惠券ID
     */
    private Long userCouponId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单类型
     */
    private Integer type;

    /**
     * 订单来源
     */
    private Integer source;

//    /**
//     * 订单支付类型
//     */
//    private Integer payType;

//    /**
//     * 订单配送类型
//     */
//    private Integer deliveryType;
//
//    /**
//     * 订单配送方式
//     */
//    private Integer deliveryMethod;

    /**
     * 订单配送费用
     */
    private BigDecimal deliveryFee;

    /**
     * 订单配送时间
     */
    private String deliveryTime;

    /**
     * 订单商品信息
     */
    private List<ProductSkuVo> products;

}
