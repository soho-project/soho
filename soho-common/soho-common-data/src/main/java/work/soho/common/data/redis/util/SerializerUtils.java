package work.soho.common.data.redis.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import lombok.experimental.UtilityClass;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * <p>
 * JacksonRedisUtils
 * </p>
 *
 * @author livk
 * @date 2022/1/19
 */
@UtilityClass
public class SerializerUtils {

	public <T> Jackson2JsonRedisSerializer<T> getJacksonSerializer(Class<T> targetClass) {
		Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(targetClass);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		mapper.activateDefaultTyping(new DefaultBaseTypeLimitingValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
		serializer.setObjectMapper(mapper);
		return serializer;
	}

}
