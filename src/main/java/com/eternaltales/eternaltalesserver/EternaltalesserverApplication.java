package com.eternaltales.eternaltalesserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EternaltalesserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EternaltalesserverApplication.class, args);
	}

}
