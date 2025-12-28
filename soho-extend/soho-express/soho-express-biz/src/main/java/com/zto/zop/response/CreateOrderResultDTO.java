package com.zto.zop.response;

import com.zto.zop.dto.BigMarkInfoDTO;
import lombok.Data;

@Data
public class CreateOrderResultDTO {

    /** 大头笔信息 */
    private BigMarkInfoDTO bigMarkInfo;

    /** 校验码（可空） */
    private String verifyCode;

    /** 网点编码 */
    private String siteCode;

    /** 签收信息（可空，结构不确定，先保留 Object） */
    private Object signBillInfo;

    /** 网点名称 */
    private String siteName;

    /** 运单号 */
    private String billCode;

    /** 系统订单号 */
    private String orderCode;

    /** 商家订单号 */
    private String partnerOrderCode;
}
