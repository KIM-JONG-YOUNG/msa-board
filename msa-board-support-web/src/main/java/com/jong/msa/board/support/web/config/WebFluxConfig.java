package com.jong.msa.board.support.web.config;

import com.jong.msa.board.common.converter.LocalDateConverter;
import com.jong.msa.board.common.converter.LocalDateTimeConverter;
import com.jong.msa.board.common.converter.LocalTimeConverter;
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
        registry.addConverter(LocalTimeConverter.StringToLocalTime.INSTANCE);
        registry.addConverter(LocalDateConverter.StringToLocalDate.INSTANCE);
        registry.addConverter(LocalDateTimeConverter.StringToLocalDateTime.INSTANCE);
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
