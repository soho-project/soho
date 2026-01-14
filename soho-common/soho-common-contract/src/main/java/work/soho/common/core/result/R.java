package work.soho.common.core.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel("通用返回对象")
public class R<T> {

	/**
	 * 返回状态码
	 */
	@ApiModelProperty(name = "状态码", value = "状态码", required = true, example = "2000")
	private final int code;

	/**
	 * 返回消息
	 */
	@ApiModelProperty(name = "返回消息", value = "返回消息", example = "success")
	private final String msg;

	/**
	 * 返回数据
	 */
	@ApiModelProperty(name = "返回数据", value = "返回数据")
	private final T payload;

	private R(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.payload = data;
	}

	public static <T> R<T> result(int code, String msg, T data) {
		return new R<>(code, msg, data);
	}

	public static <T> R<T> success() {
		return success(null);
	}

	public static <T> R<T> success(T data) {
		return result(BaseErrorCode.SUCCESS_CODE.code(), Constant.SUCCESS, data);
	}

	public static <T> R<T> error(int code, String msg) {
		return error(code, msg, null);
	}

	public static <T> R<T> error(int code, String msg, T data) {
		return result(code, msg, data);
	}

	public static <T> R<T> error(String msg, T data) {
		return error(BaseErrorCode.ERROR_CODE.code(), msg, data);
	}

	public static <T> R<T> error(String msg) {
		return error(msg, null);
	}

	public static <T> R<T> error() {
		return error(Constant.ERROR);
	}

	public static <T> R<T> error(ErrorCode errorCode) {
		return error(errorCode.code(), errorCode.message());
	}

	public static class Constant {

		private Constant() {
		}

		public static final String SUCCESS = "success";

		public static final String ERROR = "error";
	}

}
