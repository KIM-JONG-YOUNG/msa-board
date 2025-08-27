package com.jong.msa.board.support.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.jong.msa.board.support.web.config.WebFluxConfig;
import com.jong.msa.board.support.web.config.WebMvcConfig;
import com.jong.msa.board.support.web.filter.WebFluxLoggingFilter;
import com.jong.msa.board.support.web.filter.WebMvcLoggingFilter;
import com.jong.msa.board.support.web.handler.WebFluxErrorHandler;
import com.jong.msa.board.support.web.handler.WebMvcErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(
    classes = WebTestContext.class,
    properties = {
        "spring.main.web-application-type=servlet"
    })
public class WebMvcTests {

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoSpyBean
    private WebMvcLoggingFilter webMvcLoggingFilter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void LoggingFilter_테스트() throws Exception {

        reset(webMvcLoggingFilter);
        mockMvc.perform(get("/api/get"))
            .andDo(print());

        verify(webMvcLoggingFilter).doFilter(any(), any(), any());

        reset(webMvcLoggingFilter);
        mockMvc.perform(post("/api/post]")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

        verify(webMvcLoggingFilter).doFilter(any(), any(), any());
    }

    @Test
    void 조건부_Bean_등록_테스트() {

        WebTestContext.assertBeanExists(applicationContext, WebMvcConfig.class);
        WebTestContext.assertBeanExists(applicationContext, WebMvcLoggingFilter.class);
        WebTestContext.assertBeanExists(applicationContext, WebMvcErrorHandler.class);

        WebTestContext.assertBeanNotExists(applicationContext, WebFluxConfig.class);
        WebTestContext.assertBeanNotExists(applicationContext, WebFluxLoggingFilter.class);
        WebTestContext.assertBeanNotExists(applicationContext, WebFluxErrorHandler.class);
    }

}
