package com.jong.msaboard.support.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.support.web.condition.ConditionalOnWebMvcSecurity;
import com.jong.msaboard.support.web.filter.WebMvcTokenAuthFilter;
import com.jong.msaboard.support.web.handler.WebMvcSecurityErrorHandler;
import com.jong.msaboard.support.web.properties.TokenProperties;
import com.jong.msaboard.support.web.service.TokenMvcService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnWebMvcSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(TokenProperties.class)
public class WebMvcSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity httpSecurity,
        TokenMvcService tokenMvcService,
        ObjectMapper objectMapper,
        WebMvcSecurityErrorHandler errorHandler
    ) throws Exception {
        var securityAuthFilter = new WebMvcTokenAuthFilter(tokenMvcService, objectMapper);
        return httpSecurity
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .httpBasic(HttpBasicConfigurer<HttpSecurity>::disable)
            .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
            .logout(LogoutConfigurer<HttpSecurity>::disable)
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request.anyRequest().permitAll())
            .addFilterBefore(securityAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(errorHandler)
                .accessDeniedHandler(errorHandler))
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
