package com.zto.zop.request;

import lombok.Data;

/**
 * 查询物流轨迹请求参数
 */
@Data
public class QueryTrackBillRequest {
    private String billCode;
    private String mobilePhone;
}
