package com.jong.msaboard.support.infra.config;

import com.jong.msaboard.common.constants.DateTimeFormatters;
import com.jong.msaboard.support.infra.condition.ConditionalOnFeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

@Configuration
@ConditionalOnFeignClient
@EnableFeignClients(basePackages = "com.jong.msaboard")
public class FeignClientConfig {

    @Bean
    FeignFormatterRegistrar feignFormatterRegistrar() {
        return registry -> {
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setTimeFormatter(DateTimeFormatters.TIME);
            registrar.setDateFormatter(DateTimeFormatters.DATE);
            registrar.setDateTimeFormatter(DateTimeFormatters.DATE_TIME);
            registrar.registerFormatters(registry);
        };
    }

}
