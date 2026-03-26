package com.medhead.emergency_responder.repository;

import com.medhead.emergency_responder.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    @Query("SELECT h FROM Hospital h JOIN h.specialisms s WHERE s = :specialism AND h.availableBeds > 0")
    List<Hospital> findAvailableBySpecialism(@Param("specialism") String specialism);
}