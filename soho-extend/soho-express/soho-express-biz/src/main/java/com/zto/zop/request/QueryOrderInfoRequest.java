package com.zto.zop.request;

import lombok.Data;

@Data
public class QueryOrderInfoRequest {
    private String billCode;
    private String orderCode;
    private Integer type; // 0: 预约件， 1 全网件
}
