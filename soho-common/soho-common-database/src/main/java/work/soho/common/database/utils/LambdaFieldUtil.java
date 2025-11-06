package work.soho.common.database.utils;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lambda 表达式工具类
 */
public class LambdaFieldUtil {

    /**
     * 通过 Lambda 表达式获取属性名
     */
    private static <T> String getFieldName(SFunction<T, ?> lambda) {
        LambdaMeta meta = LambdaUtils.extract(lambda);
        return meta.getImplMethodName().substring(3); // 去掉 "get" 前缀
    }

    /**
     * 获取属性名（转换为小写）
     */
    public static <T> String getFieldNameLower(SFunction<T, ?> lambda) {
        String fieldName = getFieldName(lambda);
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    /**
     * List 转 Map
     */
    public static <T> Map<String, SFunction<T, ?>> listToMap(List<SFunction<T, ?>> list) {
        return list.stream().collect(Collectors.toMap(LambdaFieldUtil::getFieldNameLower, item-> item));
    }
}