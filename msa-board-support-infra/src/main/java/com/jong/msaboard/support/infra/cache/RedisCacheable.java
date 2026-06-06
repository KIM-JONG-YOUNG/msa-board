package com.jong.msaboard.support.infra.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.support.infra.condition.ConditionalOnRedis;
import com.jong.msaboard.support.infra.utils.MethodAspectUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {

    String name();

    String key();

    long ttl() default 60;

    @Aspect
    @Component
    @ConditionalOnRedis
    @RequiredArgsConstructor
    @Order(Ordered.HIGHEST_PRECEDENCE)
    class Advisor {

        private final RedisTemplate<String, String> redisTemplate;

        private final ObjectMapper objectMapper;

        @Around("@annotation(com.jong.msaboard.support.infra.cache.RedisCacheable)")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

            // 어노테이션 및 메서드 정보 추출
            var annotation = MethodAspectUtils.getAnnotation(joinPoint, RedisCacheable.class);
            var returnType = MethodAspectUtils.getReturnType(joinPoint);

            // void 타입의 메서드의 경우 캐싱하지 않음
            if (returnType == Void.class || returnType == void.class) {
                return joinPoint.proceed();
            }

            // redis key 변환
            var cacheKey = StringUtils.hasText(annotation.key())
                ? annotation.name() + MethodAspectUtils.getParameter(joinPoint, annotation.key())
                : annotation.name();

            // redis 상의 값이 존재할 경우 반환
            var cacheValue = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.hasText(cacheValue)) {
                return objectMapper.readValue(cacheValue, returnType);
            }

            // 메서드 실행 후 실행 결과가 존재할 경우 redis 저장 후 반환
            var result = joinPoint.proceed();
            if (result != null) {
                var resultJson = objectMapper.writeValueAsString(result);
                var cacheDuration = Duration.ofSeconds(annotation.ttl());
                redisTemplate.opsForValue().set(cacheKey, resultJson, cacheDuration);
            }
            return result;
        }
    }

}
