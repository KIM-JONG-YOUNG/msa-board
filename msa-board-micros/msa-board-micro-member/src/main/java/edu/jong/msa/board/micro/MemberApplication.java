package edu.jong.msa.board.micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.jong.msa.board.common.BoardConstants.Packages;

@SpringBootApplication(scanBasePackages = Packages.ROOT_PACKAGE)
public class MemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberApplication.class, args);
	}
	
}
