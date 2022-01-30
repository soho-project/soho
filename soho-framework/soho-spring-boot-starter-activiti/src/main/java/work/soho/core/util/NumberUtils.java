package work.soho.core.util;

import cn.hutool.core.util.StrUtil;

public class NumberUtils {

    public static Long parseLong(String str) {
        return StrUtil.isNotEmpty(str) ? Long.valueOf(str) : null;
    }


}
