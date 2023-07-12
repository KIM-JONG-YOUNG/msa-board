package edu.jong.msa.board.micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import edu.jong.msa.board.common.BoardConstants.Packages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = Packages.ROOT_PACKAGE)
@EnableConfigurationProperties(TokenApplication.Properties.class)
public class TokenApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokenApplication.class, args);
	}

	@Getter
	@ToString
	@ConstructorBinding
	@RequiredArgsConstructor
	@ConfigurationProperties(prefix = "token")
	public static final class Properties {

		private final String accessSecretKey;
		private final long accessExpireSeconds;

		private final String refreshSecretKey;
		private final long refreshExpireSeconds;
	
	}
}
