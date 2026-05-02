package com.medhead.emergency_responder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor; // Ajout
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class EmergencyResponderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmergencyResponderApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("MedHeadAsync-");
        executor.initialize();
        return executor;
    }
}