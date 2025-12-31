package com.yundasys.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Response< T> {

    /** 是否成功 */
    private Boolean result;

    /** 状态码 */
    private String code;

    /** 返回消息 */
    private String message;

    /** 返回数据 */
    private T data;

    @JsonProperty("sub_code")
    private String subCode;

    @JsonProperty("sub_msg")
    private String subMsg;
}
