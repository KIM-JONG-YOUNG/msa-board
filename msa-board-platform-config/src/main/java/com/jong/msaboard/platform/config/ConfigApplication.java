package com.jong.msaboard.platform.config;

import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@Slf4j
@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(ConfigApplication.class, args);
        var environment = context.getEnvironment();
        var configLocation = environment.getProperty("spring.cloud.config.server.native.search-locations");
        log.info("설정 파일 경로: {}", Paths.get(configLocation).toAbsolutePath().normalize());
    }

}
