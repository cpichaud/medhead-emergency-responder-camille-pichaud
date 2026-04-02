package com.medhead.emergency_responder.service;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final RestTemplate restTemplate;

    public HospitalService(HospitalRepository hospitalRepository, RestTemplate restTemplate) {
        this.hospitalRepository = hospitalRepository;
        this.restTemplate = restTemplate;
    }

    public Optional<Hospital> findBestHospital(String specialism, double pLat, double pLon) {
        List<Hospital> candidates = hospitalRepository.findAvailableBySpecialism(specialism);
        return candidates.stream().min(Comparator.comparingDouble(h -> getOsrmDistance(pLat, pLon, h.getLatitude(), h.getLongitude())));
    }

    private double getOsrmDistance(double startLat, double startLon, double endLat, double endLon) {
        try {
            String url = String.format("http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false", startLon, startLat, endLon, endLat);
            
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            return root.path("routes").get(0).path("distance").asDouble();
            
        } catch (Exception e) {
            return Math.sqrt(Math.pow(endLat - startLat, 2) + Math.pow(endLon - startLon, 2)); 
        }
    }
}