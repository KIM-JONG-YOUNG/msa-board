package com.jong.msa.board.microservice.search;

import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.constants.PackageNames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
public class SearchApplication {

    public static void main(String[] args) {
        System.setProperty("spring.application.name", MicroserviceNames.SEARCH_MICROSERVICE);
        SpringApplication.run(SearchApplication.class, args);
    }

}
