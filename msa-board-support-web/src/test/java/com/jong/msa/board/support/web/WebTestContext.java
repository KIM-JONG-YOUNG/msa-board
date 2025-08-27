package com.jong.msa.board.support.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.common.utils.PortUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = PackageNames.ROOT_PACKAGE)
public class WebTestContext {

    public static final WireMockServer WIRE_MOCK_SERVER;

    public static final int WIRE_MOCK_SERVER_PORT;

    static {
        WIRE_MOCK_SERVER_PORT = PortUtils.getAvailablePort();
        WIRE_MOCK_SERVER = new WireMockServer(WIRE_MOCK_SERVER_PORT);
    }

    public static void assertBeanExists(ApplicationContext applicationContext, Class<?> clazz) {
        assertDoesNotThrow(() -> applicationContext.getBean(clazz));
    }

    public static void assertBeanNotExists(ApplicationContext applicationContext, Class<?> clazz) {
        assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(clazz));
    }

    @PostConstruct
    void init() {
        log.info("WireMock Server Starting...");
        WIRE_MOCK_SERVER.start();
        log.info("WireMock Server Started. (port: {})", WIRE_MOCK_SERVER_PORT);
    }

    @PreDestroy
    void destroy() {
        log.info("WireMock Server Stoping... (port: {})", WIRE_MOCK_SERVER_PORT);
        WIRE_MOCK_SERVER.stop();
        log.info("WireMock Server Stoped.");
    }

}
