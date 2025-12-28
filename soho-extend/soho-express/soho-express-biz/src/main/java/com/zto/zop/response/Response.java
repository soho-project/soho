package com.zto.zop.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Response< T> {

    /** 是否成功 */
    private Boolean status;

    /** 状态码 */
    private String statusCode;

    /** 返回消息 */
    private String message;

    /** 业务结果 */
    private T result;

    private T data;
}
