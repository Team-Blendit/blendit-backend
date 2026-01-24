package kr.blendit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "kr.blendit")
@ConfigurationPropertiesScan(basePackages = "kr.blendit")
public class BlenditApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlenditApiApplication.class, args);
	}

}
