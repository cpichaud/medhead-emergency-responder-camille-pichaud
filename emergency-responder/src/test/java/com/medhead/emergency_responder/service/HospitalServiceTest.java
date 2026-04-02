package com.medhead.emergency_responder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HospitalService hospitalService;

    private Hospital hospitalA;
    private Hospital hospitalB;
    private ObjectMapper objectMapper;

  @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        hospitalA = new Hospital();
        hospitalA.setName("Hôpital Proche");
        hospitalA.setLatitude(48.8566);
        hospitalA.setLongitude(2.3522);

        hospitalB = new Hospital();
        hospitalB.setName("Hôpital Loin");
        hospitalB.setLatitude(45.7640);
        hospitalB.setLongitude(4.8357);
    }

    @Test
    void findBestHospital_ShouldReturnEmpty_WhenNoHospitalAvailable() {
        when(hospitalRepository.findAvailableBySpecialism("Cardiology")).thenReturn(Collections.emptyList());

        Optional<Hospital> result = hospitalService.findBestHospital("Cardiology", 48.0, 2.0);

        assertTrue(result.isEmpty(), "Le résultat devrait être vide si aucun hôpital ne correspond");
    }

    @Test
    void findBestHospital_ShouldReturnClosestHospital_UsingOsrmAPI() throws Exception {
        when(hospitalRepository.findAvailableBySpecialism("Cardiology")).thenReturn(Arrays.asList(hospitalA, hospitalB));

        JsonNode mockResponseClose = objectMapper.readTree("{\"routes\":[{\"distance\":1500.0}]}");
        JsonNode mockResponseFar = objectMapper.readTree("{\"routes\":[{\"distance\":50000.0}]}");

        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenReturn(mockResponseClose)
                .thenReturn(mockResponseFar);

        Optional<Hospital> result = hospitalService.findBestHospital("Cardiology", 48.0, 2.0);

        assertTrue(result.isPresent());
        assertEquals("Hôpital Proche", result.get().getName());
    }

    @Test
    void findBestHospital_ShouldUseEuclideanFallback_WhenOsrmApiFails() {
        when(hospitalRepository.findAvailableBySpecialism("Cardiology")).thenReturn(Arrays.asList(hospitalA, hospitalB));
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenThrow(new RuntimeException("OSRM API DOWN"));

        double patientLat = 48.8500;
        double patientLon = 2.3500;

        Optional<Hospital> result = hospitalService.findBestHospital("Cardiology", patientLat, patientLon);

        assertTrue(result.isPresent());
        assertEquals("Hôpital Proche", result.get().getName());
    }
}