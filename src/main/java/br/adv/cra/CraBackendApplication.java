package br.adv.cra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class CraBackendApplication {
	public static void main(String[] args) {
		System.setProperty("java.io.tmpdir", "/tmp");
		SpringApplication.run(CraBackendApplication.class, args);
	}
}