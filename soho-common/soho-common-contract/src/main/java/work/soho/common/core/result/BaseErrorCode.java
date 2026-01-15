package work.soho.common.core.result;

import work.soho.common.core.annotation.ErrorCodeGroup;

@ErrorCodeGroup
public enum BaseErrorCode implements ErrorCode {
    SUCCESS_CODE(2000, "success", "success"),
    UNAUTHORIZED_ACCESS(2401,"Unauthorized access", "未授权访问，接口无权限访问"),
    ERROR_CODE(5001,"error", "error"),
    PARAM_ERROR_CODE(5002,"Invalid parameter", "Invalid parameter");

    private final int code;
    private final String message;
    private final String description;

    BaseErrorCode(int code, String message, String description) { this.code = code; this.message = message; this.description = description; }
    public int code() { return code; }
    public String message() { return message; }
    public String description() { return description; }
}
