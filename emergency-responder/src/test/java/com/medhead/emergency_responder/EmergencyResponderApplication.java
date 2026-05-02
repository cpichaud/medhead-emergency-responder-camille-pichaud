package com.medhead.emergency_responder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor; // Ajout
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.Executor; // Ajout

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
        executor.setCorePoolSize(50);      // 50 threads prêts en permanence
        executor.setMaxPoolSize(100);     // Jusqu'à 100 threads si besoin
        executor.setQueueCapacity(10000); // File d'attente géante pour encaisser les pics
        executor.setThreadNamePrefix("MedHeadAsync-");
        executor.initialize();
        return executor;
    }
}