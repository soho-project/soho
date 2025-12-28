package com.zto.zop.request;

import lombok.Data;

@Data
public class CancelOrderRequest {
    /**
     * 取消类型 1不想寄了,2下错单,3重复下单,4运费太贵,5无人联系,6取件太慢,7态度差
     */
    private String cancelType = "1";

    // 二选一
    private String orderCode;
    private String billCode;
}
