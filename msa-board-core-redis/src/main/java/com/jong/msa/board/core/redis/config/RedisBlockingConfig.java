package com.jong.msa.board.core.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnBooleanProperty(name = "spring.data.redis.reactive", havingValue = false)
public class RedisBlockingConfig {

    @Bean
    @Primary
    RedissonClient redissonClient(RedisProperties properties) {
        String address = "redis://" + properties.getHost() + ":" + properties.getPort();
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useSingleServer().setAddress(address);
        return Redisson.create(config);
    }

    @Bean
    @Primary
    RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
        return new LettuceConnectionFactory(properties.getHost(), properties.getPort());
    }

    @Bean
    @Primary
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(StringRedisSerializer.UTF_8);
        return redisTemplate;
    }

}
