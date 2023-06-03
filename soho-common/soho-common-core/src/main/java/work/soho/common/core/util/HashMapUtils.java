package work.soho.common.core.util;

import lombok.experimental.UtilityClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UtilityClass
public class HashMapUtils {
    /**
     * list to hashmap
     *
     * @param list 传入的list
     * @param fieldName key字段名
     * @return
     */
    public HashMap<?, ?> fromList(List<?> list, String fieldName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        HashMap<Object, Object> hashMap = new HashMap<>();
        for (Object item:list) {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = item.getClass().getMethod(getter, new Class[] {});
            Object key = method.invoke(item, new Object[] {});
            hashMap.put(key, item);
        }
        return hashMap;
    }

    /**
     * hashmap to list
     *
     * @param hashMap
     * @return
     */
    public List<Object> valueToList(HashMap<?, ?> hashMap) {
        List<Object> list = new ArrayList<>();
        if(hashMap != null) {
            for (Object key: hashMap.keySet()
            ) {
                list.add(hashMap.get(key));
            }
        }
        return list;
    }
}
