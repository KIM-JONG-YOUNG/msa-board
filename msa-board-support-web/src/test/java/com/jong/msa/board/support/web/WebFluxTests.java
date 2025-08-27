package com.jong.msa.board.support.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.jong.msa.board.support.web.config.WebFluxConfig;
import com.jong.msa.board.support.web.config.WebMvcConfig;
import com.jong.msa.board.support.web.filter.WebFluxLoggingFilter;
import com.jong.msa.board.support.web.filter.WebMvcLoggingFilter;
import com.jong.msa.board.support.web.handler.WebFluxErrorHandler;
import com.jong.msa.board.support.web.handler.WebMvcErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest(
    classes = WebTestContext.class,
    properties = {
        "spring.main.web-application-type=reactive"
    })
public class WebFluxTests {

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoSpyBean
    private WebFluxLoggingFilter webFluxLoggingFilter;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void LoggingReactiveFilter_테스트() {

        reset(webFluxLoggingFilter);
        webTestClient.get().uri("/api/get")
            .exchange()
            .expectStatus().isNoContent();

        verify(webFluxLoggingFilter).filter(any(), any());

        reset(webFluxLoggingFilter);
        webTestClient.post().uri("/api/post")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent();

        verify(webFluxLoggingFilter).filter(any(), any());
    }

    @Test
    void 조건부_Bean_등록_테스트() {

        WebTestContext.assertBeanExists(applicationContext, WebFluxConfig.class);
        WebTestContext.assertBeanExists(applicationContext, WebFluxLoggingFilter.class);
        WebTestContext.assertBeanExists(applicationContext, WebFluxErrorHandler.class);

        WebTestContext.assertBeanNotExists(applicationContext, WebMvcConfig.class);
        WebTestContext.assertBeanNotExists(applicationContext, WebMvcLoggingFilter.class);
        WebTestContext.assertBeanNotExists(applicationContext, WebMvcErrorHandler.class);
    }

}
