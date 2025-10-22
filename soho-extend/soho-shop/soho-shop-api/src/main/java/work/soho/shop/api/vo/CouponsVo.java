package work.soho.shop.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CouponsVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * shop_id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer shopId;

    /**
     * name
     */
    private String name;

    /**
     * code
     */
    private String code;

    /**
     * type
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    /**
     * description
     */
    private String description;

    /**
     * min_order_amount
     */
    private BigDecimal minOrderAmount;

    /**
     * discount_value
     */
    private BigDecimal discountValue;

    /**
     * max_discount_amount
     */
    private BigDecimal maxDiscountAmount;

    /**
     * total_quantity
     */
    private Integer totalQuantity;

    /**
     * used_quantity
     */
    private Integer usedQuantity;

    /**
     * limit_per_user
     */
    private Integer limitPerUser;

    /**
     * valid_from
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;

    /**
     * valid_to
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validTo;

    /**
     * status
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
     * apply_scope
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer applyScope;

    /**
     *  ranges
     */
    private List<CouponApplyScope> ranges;

    @Data
    public static class CouponApplyScope {
        /**
         * id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long id;

        /**
         * scope_name
         */
        private String scopeName;

        /**
         * coupon_id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long couponId;

        /**
         * shop_id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer shopId;

        /**
         * scope_type
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer scopeType;

        /**
         * scope_id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long scopeId;
    }
}
