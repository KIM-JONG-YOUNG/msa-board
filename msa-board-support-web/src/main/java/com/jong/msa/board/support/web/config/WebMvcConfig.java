package com.jong.msa.board.support.web.config;

import com.jong.msa.board.support.web.converter.JavaTimeParamConverter;
import com.jong.msa.board.support.web.filter.WebMvcLoggingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcConfig implements WebMvcConfigurer {

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

    @Bean
    FilterRegistrationBean<WebMvcLoggingFilter> webMvcLoggingFilterRegistration(WebMvcLoggingFilter filter) {
        FilterRegistrationBean<WebMvcLoggingFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }

}
