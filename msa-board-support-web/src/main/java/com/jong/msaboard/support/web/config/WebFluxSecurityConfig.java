package com.jong.msaboard.support.web.config;

import com.jong.msaboard.support.web.condition.ConditionalOnWebFluxSecurity;
import com.jong.msaboard.support.web.filter.WebFluxTokenAuthFilter;
import com.jong.msaboard.support.web.handler.WebFluxSecurityErrorHandler;
import com.jong.msaboard.support.web.properties.TokenProperties;
import com.jong.msaboard.support.web.service.TokenFluxService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@ConditionalOnWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(TokenProperties.class)
public class WebFluxSecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity httpSecurity,
        TokenFluxService tokenService,
        WebFluxSecurityErrorHandler errorHandler
    ) {
        var securityAuthFilter = new WebFluxTokenAuthFilter(tokenService);
        return httpSecurity
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
            .addFilterAt(securityAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(errorHandler)
                .accessDeniedHandler(errorHandler))
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> {
            throw new RuntimeException("해당 서비스는 제공되지 않습니다.");
        };
    }

}
