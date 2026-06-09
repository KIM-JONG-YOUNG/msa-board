package com.jong.msaboard.support.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jong.msaboard.common.constants.RedisKeyPrefixes;
import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.support.web.config.WebFluxConfig;
import com.jong.msaboard.support.web.config.WebFluxSecurityConfig;
import com.jong.msaboard.support.web.converter.SecretKeyConverter;
import com.jong.msaboard.support.web.exception.ErrorCodeException;
import com.jong.msaboard.support.web.factory.EmbeddedRedisServerFactory;
import com.jong.msaboard.support.web.properties.TokenProperties;
import com.jong.msaboard.support.web.service.TokenFluxService;
import com.jong.msaboard.support.web.utils.TokenUtils;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;

@WebFluxTest
@ImportAutoConfiguration(classes = {
    RedisAutoConfiguration.class,
    RedisReactiveAutoConfiguration.class
})
@ContextConfiguration(classes = {
    WebFluxConfig.class,
    WebFluxSecurityConfig.class,
    SecretKeyConverter.class,
    TokenFluxService.class
})
@TestPropertySource(properties = {
    "spring.main.web-application-type=reactive",
    "jwt.accessToken.secretKey=accessTokenSecretKey",
    "jwt.accessToken.validDuration=60s",
    "jwt.refreshToken.secretKey=refreshTokenSecretKey",
    "jwt.refreshToken.validDuration=60m",
})
public class TokenFluxServiceTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.create();

    @Autowired
    TokenFluxService tokenFluxService;

    @Autowired
    TokenProperties tokenProperties;

    @Autowired
    ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {
        REDIS_SERVER.start();
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> REDIS_SERVER.ports().getFirst());
    }

    @AfterAll
    static void afterAll() {
        REDIS_SERVER.stop();
    }

    @Test
    void AccessToken_검증_성공_테스트() {

        var memberId = UUID.randomUUID();
        var memberGroup = Group.ADMIN;
        var accessTokenSecretKey = tokenProperties.accessToken().secretKey();
        var accessTokenValidDuration = tokenProperties.accessToken().validDuration();
        var accessToken = TokenUtils.generateToken(memberId, memberGroup, accessTokenSecretKey, accessTokenValidDuration);

        var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
        var accessTokenGenerateMono = Mono.just(accessToken)
            .then(reactiveRedisTemplate.opsForSet().add(whitelistTokenCacheKey, accessToken))
            .then(reactiveRedisTemplate.expire(whitelistTokenCacheKey, accessTokenValidDuration));

        StepVerifier.create(accessTokenGenerateMono
                .then(tokenFluxService.getAuthenticationFromAccessToken(accessToken)))
            .assertNext(auth -> {
                var authMemberId = (UUID) auth.getPrincipal();
                assertEquals(memberId, authMemberId);
            })
            .verifyComplete();

        StepVerifier.create(accessTokenGenerateMono
                .then(reactiveRedisTemplate.opsForSet().remove(whitelistTokenCacheKey, accessToken))
                .then(tokenFluxService.getAuthenticationFromAccessToken(accessToken)))
            .expectError(ErrorCodeException.class)
            .verify();

        StepVerifier.create(accessTokenGenerateMono
                .then(reactiveRedisTemplate.delete(whitelistTokenCacheKey))
                .then(tokenFluxService.getAuthenticationFromAccessToken(accessToken)))
            .expectError(ErrorCodeException.class)
            .verify();
    }

}
