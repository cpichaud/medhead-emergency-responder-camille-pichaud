package com.medhead.emergency_responder.controller;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.service.HospitalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean; 
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HospitalController.class)
class HospitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospitalService hospitalService;

    @Test
    @WithMockUser(username = "ambulance", roles = {"EMERGENCY_RESPONDER"})
    void getNearestHospital_ShouldReturn200AndHospitalJson_WhenFound() throws Exception {
        Hospital mockHospital = new Hospital();
        mockHospital.setName("Hôpital Central");
        mockHospital.setLatitude(48.85);
        mockHospital.setLongitude(2.35);

        when(hospitalService.findBestHospital("Cardiologie", 48.0, 2.0))
                .thenReturn(Optional.of(mockHospital));

        mockMvc.perform(get("/api/hospitals/nearest")
                .param("specialism", "Cardiologie")
                .param("lat", "48.0")
                .param("lon", "2.0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hôpital Central"))
                .andExpect(jsonPath("$.latitude").value(48.85));
    }

    @Test
    @WithMockUser(username = "ambulance", roles = {"EMERGENCY_RESPONDER"})
    void getNearestHospital_ShouldReturn404_WhenNotFound() throws Exception {
        when(hospitalService.findBestHospital("Neurologie", 48.0, 2.0))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/hospitals/nearest")
                .param("specialism", "Neurologie")
                .param("lat", "48.0")
                .param("lon", "2.0"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getNearestHospital_ShouldReturn401_WhenUnauthenticated() throws Exception {
        // Test prouvant que la sécurité est active
        mockMvc.perform(get("/api/hospitals/nearest")
                .param("specialism", "Cardiologie")
                .param("lat", "48.0")
                .param("lon", "2.0"))
                .andExpect(status().isUnauthorized());
    }
}