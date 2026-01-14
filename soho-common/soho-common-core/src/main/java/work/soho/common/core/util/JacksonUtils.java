package work.soho.common.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

	// 修改 ObjectMapper 配置，添加 JavaTimeModule 支持
	private static final ObjectMapper MAPPER = createObjectMapper();

	/**
	 * 创建并配置 ObjectMapper 实例
	 * @return 配置好的 ObjectMapper
	 */
	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		// 注册 Java 8 日期时间模块
		mapper.registerModule(new JavaTimeModule());

		// 禁用将日期序列化为时间戳，使用 ISO-8601 格式
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// 可选：处理空值，根据你的需求开启或关闭
		// mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		return mapper;
	}

	/**
	 * The constant JSON_EMPTY.
	 */
	public static final String JSON_EMPTY = "{}";

	/**
	 * To bean t.
	 * @param <T> the type parameter
	 * @param json the json
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
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * json to bean
	 *
	 * @param json
	 * @param typeReference
	 * @return
	 * @param <T>
	 */
	public <T> T toBean(String json, TypeReference<T> typeReference) {
		if (json == null || json.isEmpty() || typeReference == null) {
			return null;
		}
		try {
			return MAPPER.readValue(json, typeReference);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取文件反序列化为Bean
	 * @param <T> the type parameter
	 * @param inputStream inputStream
	 * @param clazz clazz
	 * @return t
	 */
	public <T> T toBean(InputStream inputStream, Class<T> clazz) {
		if (inputStream == null) {
			return null;
		}
		try {
			return MAPPER.readValue(inputStream, clazz);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 反序列化成集合属性
	 * @param <T> the type parameter
	 * @param json the json
	 * @param clazz the clazz
	 * @return the stream
	 */
	@SuppressWarnings("unchecked")
	public <T> Stream<T> toStream(String json, Class<T> clazz) {
		if (json == null || json.isEmpty() || clazz == null) {
			return Stream.empty();
		}
		CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(Collection.class, clazz);
		try {
			return ((Collection<T>) MAPPER.readValue(json, collectionType)).stream();
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Stream.empty();
	}

	/**
	 * To json string.
	 * @param obj the obj
	 * @return the string
	 */
	public String toJson(Object obj) {
		if (ObjectUtils.isEmpty(obj)) {
			return JSON_EMPTY;
		}
		try {
			return obj instanceof String ? (String) obj : MAPPER.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return JSON_EMPTY;
	}

	/**
	 * To map map.
	 * @param <K> the type parameter
	 * @param <V> the type parameter
	 * @param json the json
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
	 * @param <K> the type parameter
	 * @param <V> the type parameter
	 * @param json the json
	 * @param kClass the k class
	 * @param vClass the v class
	 * @return the map
	 */
	public <K, V> Map<K, V> toMap(byte[] json, Class<K> kClass, Class<V> vClass) {
		if (json == null || kClass == null || vClass == null) {
			return Collections.emptyMap();
		}
		try {
			MapType mapType = MAPPER.getTypeFactory().constructMapType(Map.class, kClass, vClass);
			return MAPPER.readValue(json, mapType);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取配置好的 ObjectMapper 实例
	 * 如果需要自定义配置，可以使用此方法获取 mapper 进行额外配置
	 * @return ObjectMapper 实例
	 */
	public static ObjectMapper getObjectMapper() {
		return MAPPER;
	}

}