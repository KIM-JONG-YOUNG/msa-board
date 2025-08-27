package com.jong.msa.board.platform.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    @LoadBalanced
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    Jackson2JsonEncoder jsonEncoder(ObjectMapper objectMapper) {
        return new Jackson2JsonEncoder(objectMapper);
    }

    @Bean
    Jackson2JsonDecoder jsonDecoder(ObjectMapper objectMapper) {
        return new Jackson2JsonDecoder(objectMapper);
    }

    @Bean
    CodecCustomizer codecCustomizer(Jackson2JsonEncoder jsonEncoder, Jackson2JsonDecoder jsonDecoder) {
        return configurer -> {
            configurer.defaultCodecs().jackson2JsonEncoder(jsonEncoder);
            configurer.defaultCodecs().jackson2JsonDecoder(jsonDecoder);
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .cors(corsSpec -> corsSpec.disable())
            .csrf(csrfSpec -> csrfSpec.disable())
            .formLogin(formLoginSpec -> formLoginSpec.disable())
            .httpBasic(httpBasicSpec -> httpBasicSpec.disable())
            .logout(logoutSpec -> logoutSpec.disable())
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchangeSpec -> exchangeSpec.anyExchange().permitAll())
            .build();
    }

}
