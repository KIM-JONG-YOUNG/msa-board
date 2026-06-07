package com.jong.msaboard.support.web.config;

import com.jong.msaboard.support.web.filter.WebMvcLoggingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    FilterRegistrationBean<WebMvcLoggingFilter> webMvcLoggingFilter() {
        var registration = new FilterRegistrationBean<WebMvcLoggingFilter>();
        registration.setFilter(new WebMvcLoggingFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/api/*");
        return registration;
    }

}
