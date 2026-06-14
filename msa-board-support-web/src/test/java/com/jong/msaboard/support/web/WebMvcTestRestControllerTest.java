package com.jong.msaboard.support.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.support.test.factory.EmbeddedRedisServerFactory;
import com.jong.msaboard.support.web.config.WebMvcConfig;
import com.jong.msaboard.support.web.config.WebMvcSecurityConfig;
import com.jong.msaboard.support.web.controller.WebMvcTestRestController;
import com.jong.msaboard.support.web.converter.SecretKeyConverter;
import com.jong.msaboard.support.web.error.ParamErrorCode;
import com.jong.msaboard.support.web.error.SecurityErrorCode;
import com.jong.msaboard.support.web.handler.WebMvcErrorHandler;
import com.jong.msaboard.support.web.handler.WebMvcSecurityErrorHandler;
import com.jong.msaboard.support.web.service.TokenMvcService;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import redis.embedded.RedisServer;

@WebMvcTest
@ImportAutoConfiguration(classes = {
    RedisAutoConfiguration.class
})
@ContextConfiguration(classes = {
    WebMvcConfig.class,
    WebMvcSecurityConfig.class,
    WebMvcErrorHandler.class,
    WebMvcSecurityErrorHandler.class,
    SecretKeyConverter.class,
    TokenMvcService.class,
    WebMvcTestRestController.class
})
@TestPropertySource(properties = {
    "spring.main.web-application-type=servlet",
    "jwt.accessToken.secretKey=accessTokenSecretKey",
    "jwt.accessToken.validDuration=60s",
    "jwt.refreshToken.secretKey=refreshTokenSecretKey",
    "jwt.refreshToken.validDuration=60m",
})
public class WebMvcTestRestControllerTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.createEmbeddedRedisServer();

    @Autowired
    MockMvc mockMvc;

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
    void PathVariable_유효성체크_테스트() throws Exception {

        mockMvc.perform(get("/api/mvc/validate/path/1"))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/mvc/validate/path/0"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ParamErrorCode.INVALID_PARAMETER.code()));
    }

    @Test
    void RequestParam_유효성체크_테스트() throws Exception {

        mockMvc.perform(get("/api/mvc/validate/query")
                .param("query", "value"))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/mvc/validate/query")
                .param("query", ""))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ParamErrorCode.INVALID_PARAMETER.code()));
    }

    @Test
    void ModelAttribute_유효성체크_테스트() throws Exception {

        mockMvc.perform(get("/api/mvc/validate/query/dto")
                .param("property", "value"))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/mvc/validate/query/dto")
                .param("property", ""))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ParamErrorCode.INVALID_PARAMETER.code()));
    }

    @Test
    void RequestBody_유효성체크_테스트() throws Exception {

        mockMvc.perform(post("/api/mvc/validate/body")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"property\": \"value\"}"))
            .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/mvc/validate/body")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"property\": \"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ParamErrorCode.INVALID_PARAMETER.code()));
    }

    @Test
    void Security_Method_Annotation_테스트() throws Exception {

        var memberId = UUID.randomUUID();
        var adminAccessToken = tokenMvcService.generateAccessToken(memberId, Group.ADMIN);
        var userAccessToken = tokenMvcService.generateAccessToken(memberId, Group.USER);

        mockMvc.perform(get("/api/mvc/security/admin")
                .header(HeaderNames.ACCESS_TOKEN, adminAccessToken))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/mvc/security/admin")
                .header(HeaderNames.ACCESS_TOKEN, userAccessToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(SecurityErrorCode.NOT_ACCESSIBLE_URL.code()));

        mockMvc.perform(get("/api/mvc/security/user")
                .header(HeaderNames.ACCESS_TOKEN, userAccessToken))
            .andExpect(status().isNoContent());
    }

}
