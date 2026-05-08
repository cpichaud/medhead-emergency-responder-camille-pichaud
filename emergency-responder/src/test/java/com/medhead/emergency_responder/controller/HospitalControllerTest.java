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

import static org.mockito.ArgumentMatchers.*; // Ajoute anyDouble, anyString, etc.
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
        mockHospital.setName("London Central Emergency");
        mockHospital.setAvailableBeds(8);
        mockHospital.setTravelTime(360.0);

        // Correction : Utilisation de any() ou eq() via Mockito
        when(hospitalService.findBestHospital(anyString(), anyDouble(), anyDouble()))
                .thenReturn(Optional.of(mockHospital));

        mockMvc.perform(get("/api/hospitals/nearest")
                .param("specialism", "Médecine d'urgence")
                .param("lat", "51.503")
                .param("lon", "-0.114")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("London Central Emergency"))
                .andExpect(jsonPath("$.travelTime").value(360.0));
    }

    @Test
    @WithMockUser(username = "ambulance", roles = {"EMERGENCY_RESPONDER"})
    void getNearestHospital_ShouldReturn404_WhenNotFound() throws Exception {
        when(hospitalService.findBestHospital(anyString(), anyDouble(), anyDouble()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/hospitals/nearest")
                .param("specialism", "Neurologie")
                .param("lat", "48.0")
                .param("lon", "2.0"))
                .andExpect(status().isNotFound());
    }
}