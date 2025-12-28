package com.zto.zop.request;

import lombok.Data;

/**
 * 拦截取消请求
 */
@Data
public class CancelInterceptRequest {
    private String thirdBizNo;
    private String billCode;
}
