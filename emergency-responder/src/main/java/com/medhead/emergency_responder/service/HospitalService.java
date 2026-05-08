package com.medhead.emergency_responder.service;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.repository.HospitalRepository;
import com.medhead.emergency_responder.event.BedReservationEvent;
import com.medhead.emergency_responder.dto.DistanceMatrixResponse; // On appelle le nouveau fichier ici
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class HospitalService {
    private static final Logger logger = LoggerFactory.getLogger(HospitalService.class);
    
    private final HospitalRepository hospitalRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    public HospitalService(HospitalRepository hospitalRepository, ApplicationEventPublisher eventPublisher, RestTemplate restTemplate) {
        this.hospitalRepository = hospitalRepository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    public Optional<Hospital> findBestHospital(String specialism, double pLat, double pLon) {
        // Recherche des hôpitaux proches (rayon de 0.5 degré)
        List<Hospital> candidates = hospitalRepository.findAvailableBySpecialismInArea(
                specialism, pLat - 0.5, pLat + 0.5, pLon - 0.5, pLon + 0.5);

        Hospital nearest = null;
        double shortestDuration = Double.MAX_VALUE;

        for (Hospital candidate : candidates) {
            double duration = getRouteDuration(pLat, pLon, candidate.getLatitude(), candidate.getLongitude());
            
            if (duration < shortestDuration) {
                shortestDuration = duration;
                nearest = candidate;
            }
        }

        if (nearest != null) {
            eventPublisher.publishEvent(new BedReservationEvent(this, nearest.getId(), nearest.getName()));
        }
        return Optional.ofNullable(nearest);
    }

    private double getRouteDuration(double sLat, double sLon, double eLat, double eLon) {
        // return (Math.abs(sLat - eLat) + Math.abs(sLon - eLon)) * 10000;

        String url = String.format(
            "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%f,%f&destinations=%f,%f&key=%s",
            sLat, sLon, eLat, eLon, googleMapsApiKey
        );
        logger.info("Requête Distance Matrix : départ({},{}), destination({},{})", sLat, sLon, eLat, eLon);

        try {
            DistanceMatrixResponse response = restTemplate.getForObject(url, DistanceMatrixResponse.class);
            
            if (response != null) {
                if ("OK".equals(response.status) && !response.rows.isEmpty()) {
                    double duration = response.rows.get(0).elements.get(0).duration.value;
                    logger.info("Calcul d'itinéraire validé. Durée : {} secondes", duration);
                    return duration;
                } else {
                    // Log technique en cas de refus de service (quota, clé, etc.)
                    logger.warn("Réponse API reçue mais invalide. Statut : {}", response.status);
                }
            }
        } catch (Exception e) {
            // Log d'erreur réseau (Timeouts, DNS, etc.)
            logger.error("Erreur de communication avec le fournisseur de cartographie : {}", e.getMessage());
        }

        logger.info(" calcul de distance par approximation.");
        return (Math.abs(sLat - eLat) + Math.abs(sLon - eLon)) * 10000;

    }
}