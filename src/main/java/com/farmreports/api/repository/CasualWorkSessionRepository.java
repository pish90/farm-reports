package com.farmreports.api.repository;

import com.farmreports.api.entity.CasualWorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CasualWorkSessionRepository extends JpaRepository<CasualWorkSession, Integer> {

    @Query("SELECT s FROM CasualWorkSession s LEFT JOIN FETCH s.entries e LEFT JOIN FETCH e.casualLabourer " +
           "WHERE s.farm.id = :farmId ORDER BY s.sessionDate DESC")
    List<CasualWorkSession> findByFarmIdWithEntries(@Param("farmId") Integer farmId);

    @Query("SELECT s FROM CasualWorkSession s LEFT JOIN FETCH s.entries e LEFT JOIN FETCH e.casualLabourer " +
           "WHERE s.id = :id AND s.farm.id = :farmId")
    Optional<CasualWorkSession> findByIdAndFarmId(@Param("id") Integer id, @Param("farmId") Integer farmId);
}
