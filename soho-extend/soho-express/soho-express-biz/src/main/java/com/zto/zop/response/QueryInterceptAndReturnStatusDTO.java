package com.zto.zop.response;

import lombok.Data;

@Data
public class QueryInterceptAndReturnStatusDTO {
    private String billCode;
    private String serviceCode;
    private String status;
    private String statusName;
}
