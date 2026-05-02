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
    @Async("taskExecutor")
    public void handleBedReservationEvent(BedReservationEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Réservation de lit déclenchée asynchrone - Hôpital: {} (ID: {})", 
                         event.getHospitalName(), event.getHospitalId());
        }
    }
}