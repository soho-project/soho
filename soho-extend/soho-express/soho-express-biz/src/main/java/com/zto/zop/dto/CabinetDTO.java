package com.zto.zop.dto;

import lombok.Data;

/**
 * 智能柜 DTO
 */
@Data
public class CabinetDTO {

    /** 柜地址 */
    private String address;

    /** 规格 */
    private Integer specification;

    /** 柜编码 */
    private String code;
}
