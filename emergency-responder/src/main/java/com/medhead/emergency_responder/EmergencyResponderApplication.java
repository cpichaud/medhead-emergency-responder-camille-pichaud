package com.medhead.emergency_responder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmergencyResponderApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmergencyResponderApplication.class, args);
	}

}
