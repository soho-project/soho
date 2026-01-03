package work.soho.express.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class SimpleExpressOrderDTO {
    /**
     * 业务跟踪单号
     */
    private String trackingNo;

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
     * 发件人
     *
     * 可选；如果写了发件人，请将发件人信息填写完整
     */
    private String senderName;

    /**
     * 发件人电话
     *
     * 可选
     */
    private String senderPhone;

    /**
     * 发件人手机
     * 可选
     */
    private String senderMobile;

    /**
     * 发件人省
     */
    private String senderProvince;

    /**
     * 发件人市
     */
    private String senderCity;

    /**
     * 发件人区
     */
    private String senderDistrict;

    /**
     * 发件人地址
     */
    private String senderAddress;

    /**
     * 物品详情
     */
    private List<OrderItem> orderItems = new ArrayList<>();

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
