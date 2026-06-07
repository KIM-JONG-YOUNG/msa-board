package com.jong.msaboard.support.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jong.msaboard.support.web.config.WebMvcConfig;
import com.jong.msaboard.support.web.controller.WebMvcTestRestController;
import com.jong.msaboard.support.web.error.ParamErrorCode;
import com.jong.msaboard.support.web.handler.WebMvcErrorHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ContextConfiguration(classes = {
    WebMvcConfig.class,
    WebMvcErrorHandler.class,
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

    @Autowired
    MockMvc mockMvc;

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

}
