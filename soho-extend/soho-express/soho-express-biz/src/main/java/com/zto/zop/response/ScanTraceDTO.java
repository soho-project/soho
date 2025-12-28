package com.zto.zop.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScanTraceDTO {
    /** 运单号 */
    private String billCode;

    /** 扫描类型，如收件、派件等 */
    private String scanType;

    /** 扫描时间戳（毫秒） */
    private String scanDate;

    /** 当前扫描网点信息 */
    private ScanSiteDTO scanSite;

    /** 前一/下一网点信息 */
    private ScanSiteDTO preOrNextSite;

    /** 签收人 */
    private String signMan;

    /** 操作人 */
    private String operateUser;

    /** 操作人编码 */
    private String operateUserCode;

    /** 操作人手机号 */
    private String operateUserPhone;

    /** 国家 */
    private String country;

    /** 扫描描述 */
    private String desc;

    /** 代收或代理信息 */
    private AgentInfoDTO agentInfo;

    /** 退回类型 */
    private String returnType;

    /** 退回更新类型说明 */
    private String returnUpdateType;

    private BigDecimal weight;

    private String extend;
}
