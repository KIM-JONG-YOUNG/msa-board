package com.jong.msa.board.platform.config;

import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ConfigApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String configLocationPath = environment.getProperty(
            "spring.cloud.config.server.native.search-locations");
        log.info("Config File Path={}", Paths.get(configLocationPath).toAbsolutePath());
    }

}
