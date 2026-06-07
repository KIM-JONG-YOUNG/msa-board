package com.jong.msaboard.support.web.config;

import com.jong.msaboard.support.web.condition.ConditionalOnWebFlux;
import com.jong.msaboard.support.web.filter.WebFluxLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebFlux
public class WebFluxConfig implements WebMvcConfigurer {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    WebFluxLoggingFilter webFluxLoggingFilter() {
        return new WebFluxLoggingFilter("/api/*");
    }

}
