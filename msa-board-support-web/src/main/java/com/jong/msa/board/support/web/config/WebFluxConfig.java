package com.jong.msa.board.support.web.config;

import com.jong.msa.board.support.web.converter.JavaTimeParamConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(JavaTimeParamConverter.StringToLocalTime.INSTANCE);
        registry.addConverter(JavaTimeParamConverter.StringToLocalDate.INSTANCE);
        registry.addConverter(JavaTimeParamConverter.StringToLocalDateTime.INSTANCE);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .exposedHeaders("*")
            .allowCredentials(true);
    }

}
