package com.yundasys.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CancelBillOrderResultResponse {
    @JsonProperty("order_serial_no")
    private String orderSerialNo;
    @JsonProperty("mail_no")
    private String mailNo;
    private String status;
    private String msg;
}
