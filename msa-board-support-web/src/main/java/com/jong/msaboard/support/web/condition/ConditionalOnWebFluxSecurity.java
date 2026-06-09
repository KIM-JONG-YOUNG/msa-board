package com.jong.msaboard.support.web.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnWebFlux
@ConditionalOnClass(name = {
    "org.springframework.data.redis.core.ReactiveRedisTemplate",
    "org.springframework.security.core.Authentication",
    "io.jsonwebtoken.Jwts"
})
public @interface ConditionalOnWebFluxSecurity {}
