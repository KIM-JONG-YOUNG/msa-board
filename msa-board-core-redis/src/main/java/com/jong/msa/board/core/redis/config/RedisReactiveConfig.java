package com.jong.msa.board.core.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnBooleanProperty(name = "spring.data.redis.reactive", havingValue = true)
public class RedisReactiveConfig {

    @Bean
    @Primary
    RedissonReactiveClient redissonReactiveClient(RedisProperties properties) {
        String address = "redis://" + properties.getHost() + ":" + properties.getPort();
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useSingleServer().setAddress(address);
        return Redisson.create(config).reactive();
    }

    @Bean
    @Primary
    ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisProperties properties) {
        return new LettuceConnectionFactory(properties.getHost(), properties.getPort());
    }

    @Bean
    @Primary
    ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
        ReactiveRedisConnectionFactory connectionFactory
    ) {
        return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext
            .<String, String>newSerializationContext()
            .key(StringRedisSerializer.UTF_8)
            .value(StringRedisSerializer.UTF_8)
            .hashKey(StringRedisSerializer.UTF_8)
            .hashValue(StringRedisSerializer.UTF_8)
            .build());
    }
}
