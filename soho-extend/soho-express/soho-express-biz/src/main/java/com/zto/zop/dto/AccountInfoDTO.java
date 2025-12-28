package com.zto.zop.dto;

import lombok.Data;

@Data
public class AccountInfoDTO {

    /** 账号 */
    private String accountId;

    /** 密码 */
    private String accountPassword;

    /** 账号类型 */
    private Integer type;

    /** 客户编码 */
    private String customerId;
}
