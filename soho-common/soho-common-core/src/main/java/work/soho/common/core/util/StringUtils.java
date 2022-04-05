package work.soho.common.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    /**
     * 空字符串判断
     *
     * @param cs
     * @return
     */
    public boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 非空字符串判断
     *
     * @param cs
     * @return
     */
    public boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 空白字符串判断
     *
     * @param cs
     * @return
     */
    public boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
