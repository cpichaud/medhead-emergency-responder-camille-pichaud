package com.medhead.emergency_responder.service;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.repository.HospitalRepository;
import com.medhead.emergency_responder.event.BedReservationEvent;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class HospitalService {
    private static final Logger logger = LoggerFactory.getLogger(HospitalService.class);
    private static final double SEARCH_RADIUS = 0.5;

    private final HospitalRepository hospitalRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${osrm.api.url}")
    private String osrmApiUrl;

    public HospitalService(HospitalRepository hospitalRepository, 
                           ApplicationEventPublisher eventPublisher, 
                           RestTemplate restTemplate) {
        this.hospitalRepository = hospitalRepository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    public Optional<Hospital> findBestHospital(String specialism, double pLat, double pLon) {
        List<Hospital> candidates = hospitalRepository.findAvailableBySpecialismInArea(
                specialism, pLat - SEARCH_RADIUS, pLat + SEARCH_RADIUS, 
                pLon - SEARCH_RADIUS, pLon + SEARCH_RADIUS);

        if (candidates.isEmpty()) return Optional.empty();

        Hospital nearest = null;
        double shortestDuration = Double.MAX_VALUE;

        for (Hospital candidate : candidates) {
            double duration = getOSRMRouteDuration(pLat, pLon, candidate.getLatitude(), candidate.getLongitude());
            if (duration < shortestDuration) {
                shortestDuration = duration;
                nearest = candidate;
            }
        }

        if (nearest != null) {
            nearest.setTravelTime(shortestDuration);
            eventPublisher.publishEvent(new BedReservationEvent(this, nearest.getId(), nearest.getName()));
            return Optional.of(nearest);
        }

        return Optional.empty();
    }

    private double getOSRMRouteDuration(double sLat, double sLon, double eLat, double eLon) {
        try {
            // Le "Locale.US" force le format avec un POINT décimal (ex: 51.503)
            String coordinates = String.format(Locale.US, "%.6f,%.6f;%.6f,%.6f", sLon, sLat, eLon, eLat);
            String url = osrmApiUrl + coordinates + "?overview=false";
            
            logger.info("Tentative d'appel OSRM : {}", url);
            
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("routes") && response.get("routes").size() > 0) {
                return response.get("routes").get(0).get("duration").asDouble();
            }
        } catch (Exception e) {
            logger.error("Erreur OSRM : {}", e.getMessage());
        }
        return Double.MAX_VALUE;
    }
}