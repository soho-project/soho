package work.soho.shop.api.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class ShopUserCouponsVo {
    /**
     * 我的优惠劵ID
     */
    private Long id;

    /**
     * 当前是否活跃
     */
    private Boolean isActive = Boolean.TRUE;

    /**
     * 店铺ID
     */
    private Integer shopId;

    /**
     * 优惠劵名称
     */
    private String name;

    /**
     * 有的优惠劵编码
     */
    private String code;

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
}
