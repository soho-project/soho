package work.soho.shop.api.vo;

import lombok.Data;

@Data
public class ShopVo {
    /**
     * id
     */
    private Integer id;

    /**
     * name
     */
    private String name;

    /**
     * keywords
     */
    private String keywords;

    /**
     * Introduction
     */
    private String introduction;

    /**
     * tel
     */
    private String tel;

    /**
     * address
     */
    private String address;

    /**
     * shop_qualifications_imgs
     */
    private String shopQualificationsImgs;

    /**
     * user_id
     */
    private Integer userId;
}
