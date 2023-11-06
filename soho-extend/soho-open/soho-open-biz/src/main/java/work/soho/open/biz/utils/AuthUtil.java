package work.soho.open.biz.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.util.PropertySource;
import work.soho.open.api.req.BaseAuthReq;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class AuthUtil {
    private static ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    /**
     * 加签
     *
     * @param body
     * @param secretKey
     * @return
     * @param <T>
     * @throws Exception
     */
    public static <T> String generateMD5Signature(T body, String secretKey) throws Exception {
        Field[] declaredFields = body.getClass().getDeclaredFields();
        List<Field> fields = Arrays.asList(declaredFields);
        Collections.sort(fields, Comparator.comparing(Field::getName));

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept((Set<String>) fields.stream()
                .map(f -> f.getName())
                .collect(Collectors.toList()));
        SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("signatureFilter", filter);

        ObjectNode json = objectMapper.valueToTree(body);
        String bodyStr = objectMapper.writer(filters).writeValueAsString(json);

        return DigestUtils.md5Hex(secretKey + bodyStr).toUpperCase();
    }

    private static Set<Field> getAllFields(Class<?> clazz) {
        Set<Field> fields = new LinkedHashSet<>();
        while (clazz != null && !clazz.equals(Object.class)) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 验签
     *
     * @param authReq
     * @param secretKey
     * @return
     * @param <T>
     * @throws Exception
     */
    public static <T> boolean verifyMD5Signature(BaseAuthReq<T> authReq, String secretKey) throws Exception {
        String expectedSign = generateMD5Signature(authReq.getBody(), secretKey);
        return expectedSign.equals(authReq.getSign());
    }
}
