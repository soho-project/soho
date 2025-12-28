package com.zto.zop.dto;

import lombok.Data;

/**
 * 汇总信息
 */
@Data
public class SummaryInfoDTO {

    /** 尺寸描述 */
    private String size;

    /** 件数 */
    private Integer quantity;

    /** 商品价格 */
    private Integer price;

    /** 运费 */
    private Integer freight;

    /** 保价费 */
    private Integer premium;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;
}
