package com.jong.msa.board.microservice.member;

import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.constants.PackageNames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
public class MemberApplication {

    public static void main(String[] args) {
        System.setProperty("spring.application.name", MicroserviceNames.MEMBER_MICROSERVICE);
        SpringApplication.run(MemberApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
