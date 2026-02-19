package com.google.rating.saas_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;

@SpringBootApplication(exclude = {
    org.springframework.ai.autoconfigure.azure.openai.AzureOpenAiAutoConfiguration.class,
    TaskExecutionAutoConfiguration.class,
    TaskSchedulingAutoConfiguration.class
})
@EnableScheduling
public class SaasAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaasAiApplication.class, args);
	}

}
