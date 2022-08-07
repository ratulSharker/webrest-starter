package com.webrest.common.config;

import java.time.Duration;

import com.webrest.common.dto.response.Response;
import com.webrest.common.utils.HibernateAwareFullResponseRedisSerializer;
import com.webrest.common.utils.HibernateAwareResponseDataRedisSerializer;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfiguration {

	public static final String CACHE_CONFIGURATION_FULL_RESPONSE = "full-response";
	public static final String CACHE_CONFIGURATION_DATA_ONLY = "data-only";

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {

		RedisCacheConfiguration fullResponseConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(60))
				.disableCachingNullValues()
				.serializeValuesWith(SerializationPair
						.fromSerializer(new HibernateAwareFullResponseRedisSerializer()));

		RedisCacheConfiguration dataOnlyConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(60))
				.disableCachingNullValues()
				.serializeValuesWith(SerializationPair
						.fromSerializer(new HibernateAwareResponseDataRedisSerializer()));

		return (builder) -> builder
				.withCacheConfiguration(CACHE_CONFIGURATION_FULL_RESPONSE,
						fullResponseConfiguration)
				.withCacheConfiguration(CACHE_CONFIGURATION_DATA_ONLY,
						dataOnlyConfiguration);
	}
}
