package com.yundasys.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CancelBillOrderRequest {
    private String appid;
    @JsonProperty("partner_id")
    private String partnerId;
    private String secret;
    private List< Order> orders;

    public static class Order {
        @JsonProperty("order_serial_no")
        private String orderSerialNo;
        @JsonProperty("mailno")
        private String mailNo;
    }
}
