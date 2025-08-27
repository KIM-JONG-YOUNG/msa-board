package com.jong.msa.board.core.redis.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.utils.SpELUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {

    String name();

    String key();

    long time() default 60;

    @Component
    @RequiredArgsConstructor
    @org.aspectj.lang.annotation.Aspect
    @ConditionalOnBooleanProperty(name = "spring.data.redis.reactive", havingValue = false)
    class Aspect {

        private final ObjectMapper objectMapper;

        private final RedisTemplate<String, String> redisTemplate;

        @Around("@annotation(com.jong.msa.board.core.redis.annotation.RedisCacheable)")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            RedisCacheable annotation = signature.getMethod().getAnnotation(RedisCacheable.class);

            Class<?> returnType = signature.getReturnType();
            if (returnType == Void.TYPE) {
                return joinPoint.proceed();
            }

            Map<String, Object> parameters = new HashMap<>();
            String[] parameterNames = signature.getParameterNames();
            Object[] parameterValues = joinPoint.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                parameters.put(parameterNames[i], parameterValues[i]);
            }

            Object parseValue = SpELUtils.getValue(annotation.key(), parameters);
            String cachingKey = annotation.name() + parseValue;
            Duration cachingTime = Duration.ofSeconds(annotation.time());

            String cachingValue = null;
            Object result = null;

            if (redisTemplate.hasKey(cachingKey)) {
                cachingValue = redisTemplate.opsForValue().get(cachingKey);
                result = objectMapper.readValue(cachingValue, returnType);
            } else {
                result = joinPoint.proceed();
                cachingValue = objectMapper.writeValueAsString(result);
                redisTemplate.opsForValue().set(cachingKey, cachingValue, cachingTime);
            }

            return result;
        }
    }

}
