package com.spring.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmailSchedulerBirthdayApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailSchedulerBirthdayApplication.class, args);
	}

}
