package com.jong.msaboard.support.infra.cache;

import com.jong.msaboard.support.infra.condition.ConditionalOnRedis;
import com.jong.msaboard.support.infra.utils.MethodAspectUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisEvict {

    String name();

    String key();

    @Aspect
    @Component
    @ConditionalOnRedis
    @RequiredArgsConstructor
    @Order(Ordered.LOWEST_PRECEDENCE)
    class Advisor {

        private final RedisTemplate<String, String> redisTemplate;

        @AfterReturning("@annotation(com.jong.msaboard.support.infra.cache.RedisEvict)")
        public void afterReturning(JoinPoint joinPoint) {
            var annotation = MethodAspectUtils.getAnnotation(joinPoint, RedisEvict.class);
            redisTemplate.delete(StringUtils.hasText(annotation.key())
                ? annotation.name() + MethodAspectUtils.getParameter(joinPoint, annotation.key())
                : annotation.name());
        }
    }

}
