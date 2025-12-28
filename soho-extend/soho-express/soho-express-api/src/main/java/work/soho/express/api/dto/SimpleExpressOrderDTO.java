package work.soho.express.api.dto;

import lombok.Data;

@Data
public class SimpleExpressOrderDTO {
    /**
     * receiver_name
     */
    private String receiverName;

    /**
     * receiver_phone
     */
    private String receiverPhone;

    /**
     * receiver_mobile
     */
    private String receiverMobile;

    /**
     * receiver_province
     */
    private String receiverProvince;

    /**
     * receiver_city
     */
    private String receiverCity;

    /**
     * receiver_district
     */
    private String receiverDistrict;

    /**
     * receiver_address
     */
    private String receiverAddress;

    /**
     * 物品详情
     */
    private String orderItems;

    /**
     * summary_info
     */
    private String summaryInfo;

    /**
     * 商家订单号
     */
    private String partnerOrderNo;

    /**
     * 增值服务信息
     */
    private String remark;
}
