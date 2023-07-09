package edu.jong.msa.board.micro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI openAPI(
			@Value("${springdoc.title}") String title, 
			@Value("${springdoc.version}") String version) {
		return new OpenAPI().info(new Info()
				.title(title)
				.version(version));
	}

}
