package work.soho.common.json.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The type Jackson util.
 */
@UtilityClass
public class JacksonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * The constant JSON_EMPTY.
     */
    public static final String JSON_EMPTY = "{}";

    /**
     * To bean t.
     *
     * @param <T>   the type parameter
     * @param json  the json
     * @param clazz the clazz
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public <T> T toBean(String json, Class<T> clazz) {
        if (json == null || json.isEmpty() || clazz == null) {
            return null;
        }
        try {
            return clazz.isInstance(json) ? (T) json : MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文件反序列化为Bean
     *
     * @param <T>         the type parameter
     * @param inputStream inputStream
     * @param clazz       clazz
     * @return t
     */
    public <T> T toBean(InputStream inputStream, Class<T> clazz) {
        if (inputStream == null) {
            return null;
        }
        try {
            return MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化成集合属性
     *
     * @param <T>   the type parameter
     * @param json  the json
     * @param clazz the clazz
     * @return the stream
     */
    @SuppressWarnings("unchecked")
    public <T> Stream<T> toStream(String json, Class<T> clazz) {
        if (json == null || json.isEmpty() || clazz == null) {
            return Stream.empty();
        }
        var collectionType = MAPPER.getTypeFactory().constructCollectionType(Collection.class, clazz);
        try {
            return ((Collection<T>) MAPPER.readValue(json, collectionType)).stream();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }

    /**
     * To json string.
     *
     * @param obj the obj
     * @return the string
     */
    public String toJson(Object obj) {
        if (ObjectUtils.isEmpty(obj)) {
            return JSON_EMPTY;
        }
        try {
            return obj instanceof String ? (String) obj : MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return JSON_EMPTY;
    }

    /**
     * To map map.
     *
     * @param <K>    the type parameter
     * @param <V>    the type parameter
     * @param json   the json
     * @param kClass the k class
     * @param vClass the v class
     * @return the map
     */
    @SneakyThrows
    public <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
        return toMap(json.getBytes(), kClass, vClass);
    }

    /**
     * To map map.
     *
     * @param <K>    the type parameter
     * @param <V>    the type parameter
     * @param json   the json
     * @param kClass the k class
     * @param vClass the v class
     * @return the map
     */
    public <K, V> Map<K, V> toMap(byte[] json, Class<K> kClass, Class<V> vClass) {
        if (json == null || kClass == null || vClass == null) {
            return Collections.emptyMap();
        }
        try {
            var mapType = MAPPER.getTypeFactory().constructMapType(Map.class, kClass, vClass);
            return MAPPER.readValue(json, mapType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
