package com.yundasys.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QueryTrackBillRequest {
    @JsonProperty("mailno")
    private String mailNo;
}
