package kr.blendit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "kr.blendit")
public class BlenditApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlenditApiApplication.class, args);
	}

}
