package com.jong.msa.board.platform.gateway;

import com.jong.msa.board.common.constants.PackageNames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
