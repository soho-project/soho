package work.soho.common.core.result;

import work.soho.common.core.annotation.ErrorCodeGroup;

@ErrorCodeGroup
public enum BaseErrorCode implements ErrorCode {
    SUCCESS_CODE(2000, "成功"),
    ERROR_CODE(5001,"执行错误"),
    PARAM_ERROR_CODE(5002,"参数错误");

    private final int code;
    private final String message;

    BaseErrorCode(int code, String message) { this.code = code; this.message = message; }
    public int code() { return code; }
    public String message() { return message; }
}
