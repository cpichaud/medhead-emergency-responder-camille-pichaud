package com.medhead.emergency_responder.controller;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.service.HospitalService;
import com.medhead.emergency_responder.event.BedReservationEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HospitalController {

    private final HospitalService hospitalService;
    private final ApplicationEventPublisher eventPublisher;

    public HospitalController(HospitalService hospitalService, ApplicationEventPublisher eventPublisher) {
        this.hospitalService = hospitalService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/nearest")
    public ResponseEntity<Hospital> getNearestHospital(
            @RequestParam String specialism,
            @RequestParam double lat,
            @RequestParam double lon) {

        Optional<Hospital> hospital = hospitalService.findBestHospital(specialism, lat, lon);

        if (hospital.isPresent()) {
            Hospital foundHospital = hospital.get();
            eventPublisher.publishEvent(new BedReservationEvent(this, foundHospital.getId(), foundHospital.getName()));
            return ResponseEntity.ok(foundHospital);
        }

        return ResponseEntity.notFound().build();
    }
}