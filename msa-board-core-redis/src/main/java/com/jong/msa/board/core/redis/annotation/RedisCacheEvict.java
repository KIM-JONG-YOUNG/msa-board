package com.jong.msa.board.core.redis.annotation;

import com.jong.msa.board.common.utils.SpELUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheEvict {

    String name();

    String key();

    @Component
    @RequiredArgsConstructor
    @org.aspectj.lang.annotation.Aspect
    @ConditionalOnBooleanProperty(name = "spring.data.redis.reactive", havingValue = false)
    class Aspect {

        private final RedisTemplate<String, String> redisTemplate;

        @AfterReturning("@annotation(com.jong.msa.board.core.redis.annotation.RedisCacheEvict)")
        public void afterReturn(JoinPoint joinPoint) {

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            RedisCacheEvict annotation = signature.getMethod().getAnnotation(RedisCacheEvict.class);

            Map<String, Object> parameters = new HashMap<>();
            String[] parameterNames = signature.getParameterNames();
            Object[] parameterValues = joinPoint.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                parameters.put(parameterNames[i], parameterValues[i]);
            }

            Object parseValue = SpELUtils.getValue(annotation.key(), parameters);
            String cachingKey = annotation.name() + parseValue;

            redisTemplate.delete(cachingKey);
        }
    }

}
