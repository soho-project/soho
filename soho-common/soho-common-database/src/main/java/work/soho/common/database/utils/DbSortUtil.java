package work.soho.common.database.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.io.InvalidClassException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 排序工具类
 *
 * @author fang
 */
public class DbSortUtil {

    /**
     * 解析排序数据
     *
     * 支持格式： name:asc,age:desc
     *
     * @param sort
     * @return
     */
    public static LinkedHashMap parseOrderMap(String sort) {
        LinkedHashMap<String, String> sortMap = new LinkedHashMap<>();
        if (sort != null && !sort.isEmpty()) {
            String parts[] = sort.split(",");
            for (String part : parts) {
                String[] kv = part.split(":");
                if (kv.length == 2) {
                    sortMap.put(kv[0], kv[1]);
                }
            }
        }
        return sortMap;
    }

    /**
     * 添加排序
     *
     * @param queryWrapper
     * @param list
     * @param sort
     * @param isEmptyThrow
     * @param <T>
     * @throws InvalidClassException
     */
    public static <T> void applySort(LambdaQueryWrapper<T> queryWrapper, List<SFunction<T, ?>> list, String sort, Boolean isEmptyThrow) throws InvalidClassException {
        LinkedHashMap<String, String> sortMap = parseOrderMap(sort);
        Map<String, SFunction<T, ?>> map = LambdaFieldUtil.listToMap( list);

        if (sortMap.size() > 0) {
            for (String key : sortMap.keySet()) {
                String value = sortMap.get(key);
                // 检查字段是否存在
                if (isEmptyThrow && !map.containsKey(key)) {
                    throw new InvalidClassException("排序参数非法");
                }

                if ("asc".equals(value)) {
                    queryWrapper.orderByAsc(map.get( key));
                } else {
                    queryWrapper.orderByDesc(map.get(key));
                }
            }
        }
    }

    /**
     * 添加排序
     *
     * @param queryWrapper
     * @param list
     * @param sort
     * @param <T>
     * @throws InvalidClassException
     */
    public static <T> void applySort(LambdaQueryWrapper<T> queryWrapper, List<SFunction<T, ?>> list, String sort) throws InvalidClassException {
        applySort(queryWrapper, list, sort, true);
    }

    /**
     * 添加排序
     *
     * @param queryWrapper
     * @param list
     * @param sort
     * @param <T>
     * @throws InvalidClassException
     */
    public static <T> void applySortWithoutThrow(LambdaQueryWrapper<T> queryWrapper, List<SFunction<T, ?>> list, String sort) throws InvalidClassException {
        applySort(queryWrapper, list, sort, false);
    }
}
