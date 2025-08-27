package com.jong.msa.board.microservice.comment;

import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.constants.PackageNames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
public class CommentApplication {

    public static void main(String[] args) {
        System.setProperty("spring.application.name", MicroserviceNames.COMMENT_MICROSERVICE);
        SpringApplication.run(CommentApplication.class, args);
    }

}
