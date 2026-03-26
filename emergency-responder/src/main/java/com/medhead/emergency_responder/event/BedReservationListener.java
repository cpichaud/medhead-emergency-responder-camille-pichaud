package com.medhead.emergency_responder.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BedReservationListener {

    private static final Logger logger = LoggerFactory.getLogger(BedReservationListener.class);

    @EventListener
    @Async
    public void handleBedReservationEvent(BedReservationEvent event) {
        logger.info("Événement intercepté (asynchrone)");
        logger.info("Demande de réservation d'un lit envoyée au système de l'hôpital [{}] (ID: {})", 
                    event.getHospitalName(), event.getHospitalId());
    }
}