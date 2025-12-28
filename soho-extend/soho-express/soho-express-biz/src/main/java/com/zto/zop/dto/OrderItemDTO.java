package com.zto.zop.dto;

import lombok.Data;

/**
 * 订单明细
 */
@Data
public class OrderItemDTO {

    private String name;
    private String category;
    private String material;
    private String size;

    /** 重量 */
    private Integer weight;

    /** 单价 */
    private Integer unitprice;

    /** 数量 */
    private Integer quantity;

    private String remark;
}
