package com.zto.zop.response;

import lombok.Data;

@Data
public class QueryOrderInfoResultDTO {
    /**
     * 寄件城市
     */
    private String sendCity;

    /**
     * 订单类型：0-普通订单
     */
    private String orderType;

    /**
     * 收件人姓名
     */
    private String receivName;

    /**
     * 订单备注
     */
    private String orderRemark;

    /**
     * 分配站点
     */
    private String assignSite;

    /**
     * 订单状态：99-已取消
     */
    private Integer orderStatus;

    /**
     * 寄件省份
     */
    private String sendProv;

    /**
     * 收件人电话
     */
    private String receivPhone;

    /**
     * 收件公司
     */
    private String receivCompany;

    /**
     * 分配员工手机号
     */
    private String assignEmpMobile;

    /**
     * 订单创建时间
     */
    private String orderCreateDate;

    /**
     * 收件人邮编
     */
    private String receivZipCode;

    /**
     * 收件地址
     */
    private String receivAddress;

    /**
     * 分配员工
     */
    private String assignEmp;

    /**
     * 包裹包装费
     */
    private Double parcelPackingFee;

    /**
     * 寄件人手机号
     */
    private String sendMobile;

    /**
     * 分配员工编码
     */
    private String assignEmpCode;

    /**
     * 包裹重量（克）
     */
    private Integer parcelWeight;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 包裹数量
     */
    private Integer parcelQuantity;

    /**
     * 包裹其他费用
     */
    private Double parcelOtherFee;

    /**
     * 收件人手机号
     */
    private String receivMobile;

    /**
     * 寄件人姓名
     */
    private String sendName;

    /**
     * 收件城市
     */
    private String receivCity;

    /**
     * 收件省份
     */
    private String receivProv;

    /**
     * 合作伙伴名称
     */
    private String partnerName;

    /**
     * 包裹运费
     */
    private Double parcelFreight;

    /**
     * 分配站点编码
     */
    private String assignSiteCode;

    /**
     * 寄件区县
     */
    private String sendCounty;

    /**
     * 寄件公司
     */
    private String sendCompany;

    /**
     * 增值服务收费币种
     */
    private String vasCollectCurrency;

    /**
     * 运单号
     */
    private String billCode;

    /**
     * 包裹揽收结束时间
     */
    private String parcelTakingEndTime;

    /**
     * 包裹价值
     */
    private Double parcelPrice;

    /**
     * 寄件地址
     */
    private String sendAddress;

    /**
     * 增值服务总费用
     */
    private Double vasCollectSum;

    /**
     * 二级状态
     */
    private String secStatus;

    /**
     * 收件区县
     */
    private String receivCounty;

    /**
     * 包裹揽收开始时间
     */
    private String parcelTakingStartTime;

    /**
     * 寄件人电话
     */
    private String sendPhone;

    /**
     * 包裹订单总金额
     */
    private Double parcelOrderSum;

    /**
     * 订单编码
     */
    private Long orderCode;

    /**
     * 合作伙伴ID
     */
    private String partnerId;

    /**
     * 包裹尺寸
     */
    private String parcelSize;

    /**
     * 寄件邮编（加密）
     */
    private String sendZipCode;

    /**
     * 交易ID
     */
    private String tradeId;
}
