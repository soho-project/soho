package work.soho.open.api.result;

import work.soho.common.core.annotation.ErrorCodeGroup;
import work.soho.common.core.result.ErrorCode;

@ErrorCodeGroup
public enum OpenErrorCode implements ErrorCode {
    NO_REAL_NAME_AUTHENTICATION(6000, "APP KEY ERROR", "请先完成实名认证"),
    APP_KEY_ERROR(6001, "APP KEY ERROR", "APP KEY ERROR"),
    PARAM_ERROR_CODE(6002,"SIGN ERROR", "参数错误"),

    REQUESTS_EXCEED_THE_MAXIMUM_RATE_OF_THE_APP(6104, "REQUESTS EXCEED THE MAXIMUM RATE OF THE APP", "APP请求超过最大速率"),
    INSUFFICIENT_INTERFACE_PERMISSIONS(6103, "INSUFFICIENT INTERFACE PERMISSIONS", "接口权限不足"),
    THE_APP_FAILED_THE_REVIEW(6102, "THE APP FAILED THE REVIEW", "APP未通过审核"),
    UNSUPPORTED_REQUEST_METHODS(6101, "UNSUPPORTED REQUEST METHODS", "不支持的请求方法"),
    MISSING_THE_NECESSARY_REQUEST(6100, "MISSING THE NECESSARY REQUEST", "缺少必要的请求参数"),
    USER_NOT_LOGGED_IN(6105, "USER NOT LOGGED IN", "用户未登录");

    private final int code;
    private final String message;
    private final String description;

    OpenErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
    public int code() { return code; }
    public String message() { return message; }
    public String description() { return description; }
}
