package com.yundasys.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBillOrderResultResponse {
    @JsonProperty("order_serial_no")
    private String orderSerialNo;
    @JsonProperty("mail_no")
    private String mailNo;
    private String status;
    private String remark;
    private String msg;
    private String orderId;
    @JsonProperty("pdf_info")
    private String pdfInfo;
}
