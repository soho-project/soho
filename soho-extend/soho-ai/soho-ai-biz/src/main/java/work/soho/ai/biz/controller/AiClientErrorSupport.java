package work.soho.ai.biz.controller;

import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 面向 OpenAI 兼容接口的客户端错误信息解析工具。
 */
public final class AiClientErrorSupport {
    private static final int MAX_CLIENT_MESSAGE_LENGTH = 160;
    private static final List<String> SAFE_MESSAGE_KEYWORDS = Arrays.asList(
            "钱包余额不足",
            "钱包不存在",
            "模型",
            "无效的api key",
            "当前用户没有可用的api key",
            "api key不能为空",
            "key不能为空",
            "model不能为空",
            "userId不能为空",
            "Authorization不能为空",
            "Authorization格式错误",
            "provider config not found",
            "用户信息不匹配",
            "未登录"
    );

    private AiClientErrorSupport() {
    }

    /**
     * 解析可返回给客户端的错误消息。
     *
     * @param ex 异常对象
     * @param fallbackMessage 兜底文案
     * @return 客户端可见错误消息
     */
    public static String resolveClientMessage(Throwable ex, String fallbackMessage) {
        if (ex == null) {
            return fallbackMessage;
        }
        String message = normalizeMessage(ex.getMessage());
        if (!isSafeMessage(message)) {
            return fallbackMessage;
        }
        if (containsSafeKeyword(message)) {
            return message;
        }
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            return message;
        }
        return fallbackMessage;
    }

    /**
     * 判断错误消息是否含有可透出的业务关键词。
     *
     * @param message 错误消息
     * @return true 表示属于业务可见错误
     */
    private static boolean containsSafeKeyword(String message) {
        String lowerCaseMessage = message.toLowerCase(Locale.ROOT);
        for (String keyword : SAFE_MESSAGE_KEYWORDS) {
            if (lowerCaseMessage.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断消息文本是否安全可透出。
     *
     * @param message 错误消息
     * @return true 表示可用于客户端
     */
    private static boolean isSafeMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return false;
        }
        if (message.length() > MAX_CLIENT_MESSAGE_LENGTH) {
            return false;
        }
        String lowerCaseMessage = message.toLowerCase(Locale.ROOT);
        return !lowerCaseMessage.contains("http://")
                && !lowerCaseMessage.contains("https://")
                && !lowerCaseMessage.contains("exception")
                && !lowerCaseMessage.contains("resourceaccess")
                && !lowerCaseMessage.contains("socket")
                && !lowerCaseMessage.contains("{")
                && !lowerCaseMessage.contains("}");
    }

    /**
     * 对异常消息做基础归一化处理。
     *
     * @param message 原始消息
     * @return 清洗后的消息
     */
    private static String normalizeMessage(String message) {
        if (message == null) {
            return "";
        }
        return message.replace('\n', ' ').replace('\r', ' ').trim();
    }
}
