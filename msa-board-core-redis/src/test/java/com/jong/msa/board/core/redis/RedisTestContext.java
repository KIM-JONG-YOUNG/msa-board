package com.jong.msa.board.core.redis;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

@Slf4j
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = PackageNames.ROOT_PACKAGE)
public class RedisTestContext {

    public static final RedisServer EMBEDDED_REDIS_SERVER;

    public static final int EMBEDDED_REDIS_SERVER_PORT;

    static {

        EMBEDDED_REDIS_SERVER_PORT = PortUtils.getAvailablePort();
        EMBEDDED_REDIS_SERVER = RedisServer.builder()
            .redisExecProvider(RedisExecProvider.defaultProvider()
                .override(OS.MAC_OS_X, Architecture.x86_64, "redis/redis-server")
                .override(OS.MAC_OS_X, Architecture.x86, "redis/redis-server"))
            .port(EMBEDDED_REDIS_SERVER_PORT)
            .build();

        System.setProperty("spring.data.redis.host", "localhost");
        System.setProperty("spring.data.redis.port", String.valueOf(EMBEDDED_REDIS_SERVER_PORT));
    }

    public static void assertBeanExists(ApplicationContext applicationContext, Class<?> clazz) {
        assertDoesNotThrow(() -> applicationContext.getBean(clazz));
    }

    public static void assertBeanNotExists(ApplicationContext applicationContext, Class<?> clazz) {
        assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(clazz));
    }

    @PostConstruct
    public void init() {
        log.info("Embedded Redis Server Starting...");
        EMBEDDED_REDIS_SERVER.start();
        log.info("Embedded Redis Server Started. (port: {})", EMBEDDED_REDIS_SERVER_PORT);
    }

    @PreDestroy
    public void destroy() {
        log.info("Embedded Redis Server Stoping... (port: {})", EMBEDDED_REDIS_SERVER_PORT);
        EMBEDDED_REDIS_SERVER.stop();
        log.info("Embedded Redis Server Stoped.");
    }

}
