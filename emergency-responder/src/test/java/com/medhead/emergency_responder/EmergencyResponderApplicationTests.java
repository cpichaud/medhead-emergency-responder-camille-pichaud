package com.medhead.emergency_responder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test de validation du contexte Spring Boot.
 * Ce test vérifie que l'application démarre correctement et que les Beans
 * critiques pour le projet MedHead sont bien chargés.
 */
@SpringBootTest
class EmergencyResponderApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Executor taskExecutor;

    @Test
    void contextLoads() {
        // Vérifie que le contexte Spring est bien initialisé
        assertNotNull(context, "Le contexte Spring devrait être chargé.");
    }

    @Test
    void importantBeansArePresent() {
        // Vérifie que le RestTemplate (pour OSRM) est disponible
        assertNotNull(restTemplate, "Le Bean RestTemplate pour les appels OSRM est manquant.");
        
        // Vérifie que l'Executor (pour la charge 800 req/s) est disponible
        assertNotNull(taskExecutor, "Le Bean TaskExecutor pour la haute disponibilité est manquant.");
    }
}