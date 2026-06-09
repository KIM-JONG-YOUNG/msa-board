package com.jong.msaboard.support.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.support.web.config.WebMvcConfig;
import com.jong.msaboard.support.web.config.WebMvcSecurityConfig;
import com.jong.msaboard.support.web.converter.SecretKeyConverter;
import com.jong.msaboard.support.web.exception.ErrorCodeException;
import com.jong.msaboard.support.web.factory.EmbeddedRedisServerFactory;
import com.jong.msaboard.support.web.service.TokenMvcService;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import redis.embedded.RedisServer;

@WebMvcTest
@ImportAutoConfiguration(classes = {
    RedisAutoConfiguration.class
})
@ContextConfiguration(classes = {
    WebMvcConfig.class,
    WebMvcSecurityConfig.class,
    SecretKeyConverter.class,
    TokenMvcService.class
})
@TestPropertySource(properties = {
    "spring.main.web-application-type=servlet",
    "jwt.accessToken.secretKey=accessTokenSecretKey",
    "jwt.accessToken.validDuration=60s",
    "jwt.refreshToken.secretKey=refreshTokenSecretKey",
    "jwt.refreshToken.validDuration=60m",
})
public class TokenMvcServiceTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.create();

    @Autowired
    TokenMvcService tokenMvcService;

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
    void AccessToken_생성_검증_취소_테스트() {

        var memberId = UUID.randomUUID();
        var memberGroup = Group.ADMIN;

        var accessToken = assertDoesNotThrow(() -> tokenMvcService.generateAccessToken(memberId, memberGroup));

        assertDoesNotThrow(() -> {
            var auth = tokenMvcService.getAuthenticationFromAccessToken(accessToken);
            var authMemberId = (UUID) auth.getPrincipal();
            assertEquals(memberId, authMemberId);
        });

        tokenMvcService.revokeAccessToken(accessToken);
        assertThrows(ErrorCodeException.class, () -> {
            tokenMvcService.getAuthenticationFromAccessToken(accessToken);
        });
    }

    @Test
    void RefreshToken_생성_검증_취소_테스트() {

        var memberId = UUID.randomUUID();

        var refreshToken = assertDoesNotThrow(() -> tokenMvcService.generateRefreshToken(memberId));

        assertDoesNotThrow(() -> {
            var refreshTokenMemberId = tokenMvcService.getMemberIdFromRefreshToken(refreshToken);
            assertEquals(memberId, refreshTokenMemberId);
        });

        tokenMvcService.revokeRefreshToken(refreshToken);
        assertThrows(ErrorCodeException.class, () -> tokenMvcService.getMemberIdFromRefreshToken(refreshToken));
    }


    @Test
    void 회원_Token_전체_취소() {

        var memberId = UUID.randomUUID();
        var memberGroup = Group.ADMIN;

        var accessToken = assertDoesNotThrow(() -> tokenMvcService.generateAccessToken(memberId, memberGroup));
        var refreshToken = assertDoesNotThrow(() -> tokenMvcService.generateRefreshToken(memberId));

        assertDoesNotThrow(() -> {
            tokenMvcService.getAuthenticationFromAccessToken(accessToken);
            tokenMvcService.getMemberIdFromRefreshToken(refreshToken);
        });

        tokenMvcService.revokeMemberTokenAll(memberId);
        assertThrows(ErrorCodeException.class, () -> {
            tokenMvcService.getAuthenticationFromAccessToken(accessToken);
            tokenMvcService.getMemberIdFromRefreshToken(refreshToken);
        });
    }

}
