package edu.jong.msa.board.micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import edu.jong.msa.board.common.BoardConstants.Packages;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = Packages.ROOT_PACKAGE)
public class SearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchApplication.class, args);
	}
	
}
