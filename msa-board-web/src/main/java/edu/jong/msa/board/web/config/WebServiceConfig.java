package edu.jong.msa.board.web.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.web.filter.WebLoggingFilter;

@Configuration
public class WebServiceConfig implements WebMvcConfigurer {

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		
		converters.stream()
			.filter(x -> (x instanceof MappingJackson2HttpMessageConverter))
			.map(x -> (MappingJackson2HttpMessageConverter) x)
			.forEach(x -> x.setObjectMapper(ObjectMapperUtils.getMapper()));
		
		WebMvcConfigurer.super.extendMessageConverters(converters);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/**");
		
		WebMvcConfigurer.super.addCorsMappings(registry);
	}
	
    @Bean
    FilterRegistrationBean<WebLoggingFilter> webLoggingFilter(){
        
    	FilterRegistrationBean<WebLoggingFilter> registrationBean = new FilterRegistrationBean<>();
    	registrationBean.setFilter(new WebLoggingFilter());
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.setName("web-logging-filter");
        
        return registrationBean;
    }

}
