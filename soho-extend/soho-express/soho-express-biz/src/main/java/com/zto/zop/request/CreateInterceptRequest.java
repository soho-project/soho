package com.zto.zop.request;

import lombok.Data;

@Data
public class CreateInterceptRequest {
    /** 中通运单号;必填 */
    private String billCode;

    /** 请求唯一标识；必填 */
    private String requestId;

    /** 客户编码 */
    private String customerId;

    /** 客户名称 */
    private String customerName;

    /** 收件人手机号 */
    private String receivePhone;

    /** 收件人姓名 */
    private String receiveUsername;

    /** 收件地址 */
    private String receiveAddress;

    /** 收件区 */
    private String receiveDistrict;

    /** 收件市 */
    private String receiveCity;

    /** 收件省 */
    private String receiveProvince;

    /**
     * 拦截目的地类型；必填
     * 1 返回收件网点；2 返回寄件人地址；3 修改派送地址；4 退回指定地址（必填）
     */
    private Integer destinationType;

    /** 外部业务唯一标识 */
    private String thirdBizNo;
}
