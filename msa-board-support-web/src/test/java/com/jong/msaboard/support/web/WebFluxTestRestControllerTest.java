package com.jong.msaboard.support.web;

import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.support.web.config.WebFluxConfig;
import com.jong.msaboard.support.web.config.WebFluxSecurityConfig;
import com.jong.msaboard.support.web.controller.WebFluxTestRestController;
import com.jong.msaboard.support.web.converter.SecretKeyConverter;
import com.jong.msaboard.support.web.error.ParamErrorCode;
import com.jong.msaboard.support.web.error.SecurityErrorCode;
import com.jong.msaboard.support.web.factory.EmbeddedRedisServerFactory;
import com.jong.msaboard.support.web.handler.WebFluxErrorHandler;
import com.jong.msaboard.support.web.handler.WebFluxSecurityErrorHandler;
import com.jong.msaboard.support.web.properties.TokenProperties;
import com.jong.msaboard.support.web.service.TokenFluxService;
import com.jong.msaboard.support.web.service.TokenMvcService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import redis.embedded.RedisServer;

@Slf4j
@WebFluxTest
@AutoConfigureWebTestClient
@ImportAutoConfiguration(classes = {
    RedisAutoConfiguration.class,
    RedisReactiveAutoConfiguration.class
})
@ContextConfiguration(classes = {
    WebFluxConfig.class,
    WebFluxSecurityConfig.class,
    WebFluxErrorHandler.class,
    WebFluxSecurityErrorHandler.class,
    SecretKeyConverter.class,
    TokenFluxService.class,
    WebFluxTestRestController.class
})
@TestPropertySource(properties = {
    "spring.main.web-application-type=reactive",
    "jwt.accessToken.secretKey=accessTokenSecretKey",
    "jwt.accessToken.validDuration=60s",
    "jwt.refreshToken.secretKey=refreshTokenSecretKey",
    "jwt.refreshToken.validDuration=60m",
})
public class WebFluxTestRestControllerTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.create();

    @Autowired
    WebTestClient testClient;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    TokenProperties tokenProperties;

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
    void PathVariable_유효성체크_테스트() throws Exception {

        testClient
            .get()
            .uri("/api/flux/validate/path/1")
            .exchange()
            .expectStatus().isNoContent();

        testClient
            .get()
            .uri("/api/flux/validate/path/0")
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.code").isEqualTo(ParamErrorCode.INVALID_PARAMETER.code());
    }

    @Test
    void RequestParam_유효성체크_테스트() throws Exception {

        testClient
            .get()
            .uri(builder -> builder.path("/api/flux/validate/query")
                .queryParam("query", "value")
                .build())
            .exchange()
            .expectStatus().isNoContent();

        testClient
            .get()
            .uri(builder -> builder.path("/api/flux/validate/query")
                .queryParam("query", "")
                .build())
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.code").isEqualTo(ParamErrorCode.INVALID_PARAMETER.code());
    }

    @Test
    void ModelAttribute_유효성체크_테스트() throws Exception {

        testClient
            .get()
            .uri(builder -> builder.path("/api/flux/validate/query/dto")
                .queryParam("property", "value")
                .build())
            .exchange()
            .expectStatus().isNoContent();

        testClient
            .get()
            .uri(builder -> builder.path("/api/flux/validate/query/dto")
                .queryParam("property", "")
                .build()).exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.code").isEqualTo(ParamErrorCode.INVALID_PARAMETER.code());
    }

    @Test
    void RequestBody_유효성체크_테스트() throws Exception {

        testClient
            .post()
            .uri("/api/flux/validate/body")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"property\": \"value\"}")
            .exchange()
            .expectStatus().isNoContent();

        testClient
            .post()
            .uri("/api/flux/validate/body")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"property\": \"\"}")
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.code").isEqualTo(ParamErrorCode.INVALID_PARAMETER.code());
    }

    @Test
    void Security_Method_Annotation_테스트() throws Exception {

        var memberId = UUID.randomUUID();
        var tokenMvcService = new TokenMvcService(tokenProperties, redisTemplate);
        var adminAccessToken = tokenMvcService.generateAccessToken(memberId, Group.ADMIN);
        var userAccessToken = tokenMvcService.generateAccessToken(memberId, Group.USER);

        testClient
            .get()
            .uri("/api/flux/security/admin")
            .header(HeaderNames.ACCESS_TOKEN, adminAccessToken)
            .exchange()
            .expectStatus().isNoContent();

        testClient
            .get()
            .uri("/api/flux/security/admin")
            .header(HeaderNames.ACCESS_TOKEN, userAccessToken)
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .consumeWith(result -> {
                log.info("Response: {}", new String(result.getResponseBody()));
            })
            .jsonPath("$.code").isEqualTo(SecurityErrorCode.NOT_ACCESSIBLE_URL.code());

        testClient
            .get()
            .uri("/api/flux/security/user")
            .header(HeaderNames.ACCESS_TOKEN, userAccessToken)
            .exchange()
            .expectStatus().isNoContent();
    }

}
