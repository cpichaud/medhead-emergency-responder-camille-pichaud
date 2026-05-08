package com.medhead.emergency_responder.service;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.repository.HospitalRepository;
import com.medhead.emergency_responder.event.BedReservationEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HospitalService hospitalService;

    @BeforeEach
    void setUp() throws Exception {
        // Injection de l'URL OSRM
        ReflectionTestUtils.setField(hospitalService, "osrmApiUrl", "http://test-url/");

        // Simulation d'une réponse JSON valide d'OSRM (360 secondes = 6 minutes)
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mockResponse = mapper.readTree("{\"routes\":[{\"duration\":360.0}]}");
        
        lenient().when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                 .thenReturn(mockResponse);
    }

    @Test
    void findBestHospital_ShouldReturnHospitalWithTravelTime_WhenCandidatesExist() {
        Hospital h1 = new Hospital("St Thomas", 5, 51.499, -0.118, Arrays.asList("Cardiologie"));
        h1.setId(2L);

        when(hospitalRepository.findAvailableBySpecialismInArea(anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Arrays.asList(h1));

        Optional<Hospital> result = hospitalService.findBestHospital("Cardiologie", 51.503, -0.114);

        assertTrue(result.isPresent());
        assertEquals(360.0, result.get().getTravelTime()); // Vérifie que le temps est bien injecté
        verify(eventPublisher, times(1)).publishEvent(any(BedReservationEvent.class));
    }
}