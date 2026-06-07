package com.jong.msaboard.support.web;

import com.jong.msaboard.support.web.config.WebFluxConfig;
import com.jong.msaboard.support.web.controller.WebFluxTestRestController;
import com.jong.msaboard.support.web.error.ParamErrorCode;
import com.jong.msaboard.support.web.handler.WebFluxErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@WebFluxTest
@AutoConfigureWebTestClient
@ImportAutoConfiguration(classes = {
    RedisAutoConfiguration.class,
    RedisReactiveAutoConfiguration.class
})
@ContextConfiguration(classes = {
    WebFluxConfig.class,
    WebFluxErrorHandler.class,
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

    @Autowired
    WebTestClient testClient;

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
//            .consumeWith(entityExchangeResult -> {
//                log.info("EntityExchangeResult: {}", entityExchangeResult);
//            });
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

}
