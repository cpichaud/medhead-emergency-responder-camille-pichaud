package com.medhead.emergency_responder.event;

import org.springframework.context.ApplicationEvent;

public class BedReservationEvent extends ApplicationEvent {
    private final Long hospitalId;
    private final String hospitalName;

    public BedReservationEvent(Object source, Long hospitalId, String hospitalName) {
        super(source);
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }
}