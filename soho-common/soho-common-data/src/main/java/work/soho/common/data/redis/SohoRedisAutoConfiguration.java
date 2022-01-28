//package work.soho.common.data.redis;
//
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.cache.CacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.cache.RedisCacheWriter;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import work.soho.common.data.redis.util.SerializerUtils;
//
///**
// * <p>
// * SohoRedisAutoConfiguration
// * </p>
// *
// * @author livk
// * @date 2022/1/22
// */
//@Configuration(proxyBeanMethods = false)
//@AutoConfigureBefore(RedisAutoConfiguration.class)
//public class SohoRedisAutoConfiguration {
//
//	@Bean
//	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//		redisTemplate.setKeySerializer(RedisSerializer.string());
//		redisTemplate.setHashKeySerializer(RedisSerializer.string());
//		redisTemplate.setValueSerializer(SerializerUtils.getJacksonSerializer(Object.class));
//		redisTemplate.setHashValueSerializer(SerializerUtils.getJacksonSerializer(Object.class));
//		return redisTemplate;
//	}
//
//	@Bean
//	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//		var serializer = SerializerUtils.getJacksonSerializer(Object.class);
//		return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
//				.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
//						.serializeKeysWith(
//								RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//						.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)))
//				.build();
//	}
//
//}
