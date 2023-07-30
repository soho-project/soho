package work.soho.chat.biz.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenUtils {
    /**
     * 计算字符串中Token数。
     *
     * @param text
     * @return
     */
    public int countTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 英文单词字符定义
        String englishWordRegex = "[a-zA-Z]+";
        // 中文字符定义
        String chineseWordRegex = "[\u4e00-\u9fa5]";
        // 单词计数器
        int count = 0;
        // 当前字符是否为中文字符
        boolean isChinese = false;
        // 判断每个字符是否为单词的一部分
        for (char c : text.toCharArray()) {
            // 判断当前字符是否为中文字符
            isChinese = String.valueOf(c).matches(chineseWordRegex);
            if (!isChinese && String.valueOf(c).matches(englishWordRegex)) {
                // 如果当前字符不是中文字符并且是英文字符
                // 则认为它是一个新的英文单词的开始
                count++;
            }
        }
        return count;
    }
}
