package work.soho.open.api.result;

import work.soho.common.core.annotation.ErrorCodeGroup;
import work.soho.common.core.result.ErrorCode;

@ErrorCodeGroup
public enum OpenErrorCode implements ErrorCode {
    NO_REAL_NAME_AUTHENTICATION(6000, "APP KEY ERROR", "请先完成实名认证"),
    APP_KEY_ERROR(6001, "APP KEY ERROR", "APP KEY ERROR"),
    PARAM_ERROR_CODE(6002,"SIGN ERROR", "参数错误");

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
