package com.medhead.emergency_responder.controller;

import com.medhead.emergency_responder.model.Hospital;
import com.medhead.emergency_responder.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/nearest")
    public ResponseEntity<Hospital> getNearestHospital(
            @RequestParam String specialism,
            @RequestParam double lat,
            @RequestParam double lon) {

        Optional<Hospital> hospital = hospitalService.findBestHospital(specialism, lat, lon);

        return hospital.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }
}