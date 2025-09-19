package br.adv.cra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CraBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CraBackendApplication.class, args);
	}
}