package com.zto.zop.dto;

import lombok.Data;

/**
 * 增值服务
 */
@Data
public class OrderVasDTO {

    /** 增值服务类型（如 COD） */
    private String vasType;

    /** 增值金额 */
    private Integer vasAmount;

    /** 服务费 */
    private Integer vasPrice;

    /** 服务明细 */
    private String vasDetail;

    /** 账号 */
    private String accountNo;
}
