package com.google.rating.saas_ai;

import org.springframework.boot.SpringApplication;

public class TestSaasAiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SaasAiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
