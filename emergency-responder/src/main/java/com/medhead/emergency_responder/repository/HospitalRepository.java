package com.medhead.emergency_responder.repository;

import com.medhead.emergency_responder.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    
    @Query("SELECT h FROM Hospital h JOIN h.specialisms s WHERE s = :specialism " +
           "AND h.availableBeds > 0 " +
           "AND h.latitude BETWEEN :minLat AND :maxLat " +
           "AND h.longitude BETWEEN :minLon AND :maxLon")
    List<Hospital> findAvailableBySpecialismInArea(
            @Param("specialism") String specialism,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon);
}