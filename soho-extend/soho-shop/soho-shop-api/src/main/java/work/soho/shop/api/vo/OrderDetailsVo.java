package work.soho.shop.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Data
public class OrderDetailsVo {

    /**
     * 订单信息
     */
    private OrderInfoVo order;

    /**
     * 订单商品项
     */
    private List<OrderProductItemVo> orderSkus;

    /**
     * 订单信息
     */
    @Data
    public static class OrderInfoVo {
        /**
         * id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer id;

        /**
         * no
         */
        private String no;

        /**
         * user_id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long userId;

        /**
         * amount
         */
        private BigDecimal amount;

        /**
         * product_total_amount
         */
        private BigDecimal productTotalAmount;

        /**
         * delivery_fee
         */
        private BigDecimal deliveryFee;

        /**
         * discount_amount
         */
        private BigDecimal discountAmount;

        /**
         * status
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer status;

        /**
         * pay_status
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer payStatus;

        /**
         * freight_status
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer freightStatus;

        /**
         * receiving_address
         */
        private String receivingAddress;

        /**
         * consignee
         */
        private String consignee;

        /**
         * receiving_phone_number
         */
        private String receivingPhoneNumber;

        /**
         * order_type
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer orderType;

        /**
         * source
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer source;

        /**
         * remark
         */
        private String remark;

        /**
         * updated_time
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedTime;

        /**
         * created_time
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;
    }

    /**
     * 订单商品项
     */
    @Data
    public static class OrderProductItemVo {
        private Integer id;

        /**
         * order_id
         */
        private Integer orderId;

        /**
         * product_id
         */
        private Long productId;

        /**
         * sku_id
         */
        private Integer skuId;

        /**
         * name
         */
        private String name;

        /**
         * specs
         */
        private String specs;

        /**
         * main_image
         */
        private String mainImage;

        /**
         * amount
         */
        private BigDecimal amount;

        /**
         * qty
         */
        private Integer qty;

        /**
         * total_amount
         */
        private BigDecimal totalAmount;

        /**
         * updated_time
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedTime;

        /**
         * created_time
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;
    }
}
