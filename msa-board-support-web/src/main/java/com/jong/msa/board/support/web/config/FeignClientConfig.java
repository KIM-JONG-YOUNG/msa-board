package com.jong.msa.board.support.web.config;

import com.jong.msa.board.common.constants.DateTimeFormats;
import com.jong.msa.board.common.constants.PackageNames;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

@Configuration
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClientFactory")
@EnableFeignClients(basePackages = PackageNames.ROOT_PACKAGE)
public class FeignClientConfig {

    @Bean
    FeignFormatterRegistrar feignFormatterRegistrar() {
        return registry -> {
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setTimeFormatter(DateTimeFormats.TIME_FORMATTER);
            registrar.setDateFormatter(DateTimeFormats.DATE_FORMATTER);
            registrar.setDateTimeFormatter(DateTimeFormats.DATE_TIME_FORMATTER);
            registrar.registerFormatters(registry);
        };
    }

}
