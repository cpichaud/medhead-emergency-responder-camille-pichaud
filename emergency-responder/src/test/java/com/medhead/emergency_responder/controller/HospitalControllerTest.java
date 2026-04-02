package com.medhead.emergency_responder.controller;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.service.HospitalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HospitalController.class)
@AutoConfigureMockMvc(addFilters = false)
class HospitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HospitalService hospitalService;

    @Test
    void getNearestHospital_ShouldReturn200AndHospitalJson_WhenFound() throws Exception {
        // Given
        Hospital mockHospital = new Hospital();
        mockHospital.setName("Hôpital Central");
        mockHospital.setLatitude(48.85);
        mockHospital.setLongitude(2.35);

        when(hospitalService.findBestHospital("Cardiology", 48.0, 2.0))
                .thenReturn(Optional.of(mockHospital));

        // When & Then
        mockMvc.perform(get("/api/hospitals/nearest")
                .param("specialism", "Cardiology")
                .param("lat", "48.0")
                .param("lon", "2.0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hôpital Central"))
                .andExpect(jsonPath("$.latitude").value(48.85));
    }

    @Test
    void getNearestHospital_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(hospitalService.findBestHospital("Neurology", 48.0, 2.0))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/hospitals/nearest")
                .param("specialism", "Neurology")
                .param("lat", "48.0")
                .param("lon", "2.0"))
                .andExpect(status().isNotFound());
    }
}