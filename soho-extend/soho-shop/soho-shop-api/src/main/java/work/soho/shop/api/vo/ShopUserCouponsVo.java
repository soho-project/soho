package work.soho.shop.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShopUserCouponsVo {
    /**
     * 我的优惠劵ID
     */
    private Long id;

    /**
     * 我的优惠劵编码
     */
    private String code;

    /**
     * 当前是否活跃
     */
    private Boolean isActive = Boolean.TRUE;

    /**
     * 店铺ID
     */
    private Integer shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 优惠劵名称
     */
    private String name;

    /**
     * 优惠劵描述
     */
    private String description;

    /**
     * 优惠劵开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;

    /**
     * 优惠劵结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validTo;

    /**
     * 优惠劵类型
     */
    private Integer type;

    /**
     * 优惠劵面值
     */
    private BigDecimal discountValue;

    /**
     * 优惠劵最低消费金额
     */
    private BigDecimal minOrderAmount;


}
