package com.medhead.emergency_responder.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String name;

    private int availableBeds;

    private double latitude;

    private double longitude;

    @ElementCollection
    @CollectionTable(name = "hospital_specialisms", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "specialism_name")
    private List<String> specialisms;

    public Hospital() {}

    public Hospital(String name, int availableBeds, double latitude, double longitude, List<String> specialisms) {
        this.name = name;
        this.availableBeds = availableBeds;
        this.latitude = latitude;
        this.longitude = longitude;
        this.specialisms = specialisms;
    }

    public Long getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public int getAvailableBeds() {
        return availableBeds; 
    }

    public void setAvailableBeds(int availableBeds) { 
        this.availableBeds = availableBeds; 

    }

    public double getLatitude() {
        return latitude; 
    }

    public void setLatitude(double latitude) { 
        this.latitude = latitude; 
    }

    public double getLongitude() { 
        return longitude; 
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude; 
    }

    public List<String> getSpecialisms() {
        return specialisms; 
    }

    public void setSpecialisms(List<String> specialisms) {
        this.specialisms = specialisms; 
    }
}