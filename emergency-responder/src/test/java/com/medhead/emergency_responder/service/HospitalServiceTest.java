package com.medhead.emergency_responder.service;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.repository.HospitalRepository;
import com.medhead.emergency_responder.event.BedReservationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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

    @InjectMocks
    private HospitalService hospitalService;

    private Hospital hospital1;
    private Hospital hospital2;

    @BeforeEach
    void setUp() {
        hospital1 = new Hospital("Fred Brooks Cardio", 2, 51.500, -0.100, Arrays.asList("Cardiologie"));
        hospital1.setId(16L);
        
        hospital2 = new Hospital("Julia Crusher Cardio", 5, 51.505, -0.110, Arrays.asList("Cardiologie"));
        hospital2.setId(17L);
    }

    @Test
    void findBestHospital_ShouldReturnNearestHospital_WhenCandidatesExist() {
        String specialism = "Cardiologie";
        double patientLat = 51.502;
        double patientLon = -0.105;

        when(hospitalRepository.findAvailableBySpecialismInArea(
                eq(specialism), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Arrays.asList(hospital1, hospital2));

        Optional<Hospital> result = hospitalService.findBestHospital(specialism, patientLat, patientLon);

        assertTrue(result.isPresent());
        verify(eventPublisher, times(1)).publishEvent(any(BedReservationEvent.class));
    }

    @Test
    void findBestHospital_ShouldReturnEmpty_WhenNoCandidates() {
        String specialism = "Neurologie";
        double patientLat = 51.502;
        double patientLon = -0.105;

        when(hospitalRepository.findAvailableBySpecialismInArea(
                eq(specialism), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());

        Optional<Hospital> result = hospitalService.findBestHospital(specialism, patientLat, patientLon);

        assertFalse(result.isPresent());
        verify(eventPublisher, never()).publishEvent(any(BedReservationEvent.class));
    }
}