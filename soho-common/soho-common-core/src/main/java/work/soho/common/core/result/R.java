package work.soho.common.core.result;

import lombok.Getter;

@Getter
public class R<T> {

	/**
	 * 返回状态码
	 */
	private final int code;

	/**
	 * 返回消息
	 */
	private final String msg;

	/**
	 * 返回数据
	 */
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
		return result(2000, Constant.SUCCESS, data);
	}

	public static <T> R<T> error(int code, String msg) {
		return error(code, msg, null);
	}

	public static <T> R<T> error(int code, String msg, T data) {
		return result(code, msg, data);
	}

	public static <T> R<T> error(String msg, T data) {
		return error(5001, msg, data);
	}

	public static <T> R<T> error(String msg) {
		return error(msg, null);
	}

	public static <T> R<T> error() {
		return error(Constant.ERROR);
	}

	public static class Constant {

		private Constant() {
		}

		public static final String SUCCESS = "success";

		public static final String ERROR = "error";
	}

}
