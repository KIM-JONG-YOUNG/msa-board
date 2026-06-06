package com.jong.msaboard.support.infra.config;

import com.jong.msaboard.support.infra.condition.ConditionalOnRedis;
import com.jong.msaboard.support.infra.condition.ConditionalOnRedisReactive;
import com.jong.msaboard.support.infra.condition.ConditionalOnRedisson;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    @ConditionalOnRedis
    RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
        return new LettuceConnectionFactory(properties.getHost(), properties.getPort());
    }

    @Bean
    @Primary
    @ConditionalOnRedis
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        var redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(StringRedisSerializer.UTF_8);
        return redisTemplate;
    }

    @Bean
    @Primary
    @ConditionalOnRedisReactive
    ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisProperties properties) {
        return new LettuceConnectionFactory(properties.getHost(), properties.getPort());
    }

    @Bean
    @Primary
    @ConditionalOnRedisReactive
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

    @Bean
    @Primary
    @ConditionalOnRedisson
    RedissonClient reactiveClient(RedisProperties properties) {
        var address = "redis://%s:%d".formatted(properties.getHost(), properties.getPort());
        var config = new org.redisson.config.Config();
        config.useSingleServer().setAddress(address);
        return Redisson.create(config);
    }

}
