package com.farmreports.api.repository;

import com.farmreports.api.entity.CasualWorkSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CasualWorkSessionRepository extends JpaRepository<CasualWorkSession, Integer> {

    @Query("SELECT s FROM CasualWorkSession s LEFT JOIN FETCH s.entries e LEFT JOIN FETCH e.employee " +
           "WHERE s.farm.id = :farmId ORDER BY s.sessionDate DESC")
    List<CasualWorkSession> findByFarmIdWithEntries(@Param("farmId") Integer farmId);

    // Plain (non-fetch-join) queries for pagination — a JOIN FETCH on the entries collection
    // multiplies rows per entry, which breaks LIMIT/OFFSET. entries/employee lazy-load fine
    // when mapped inside the calling @Transactional(readOnly = true) service method.
    Page<CasualWorkSession> findByFarmId(Integer farmId, Pageable pageable);

    Page<CasualWorkSession> findByFarmIdAndSessionDateBetween(
            Integer farmId, LocalDate start, LocalDate end, Pageable pageable);

    @Query("SELECT s FROM CasualWorkSession s LEFT JOIN FETCH s.entries e LEFT JOIN FETCH e.employee " +
           "WHERE s.id = :id AND s.farm.id = :farmId")
    Optional<CasualWorkSession> findByIdAndFarmId(@Param("id") Integer id, @Param("farmId") Integer farmId);

    // Used by the bulk pay importer to find its own shared per-(farm, month) placeholder session
    // (tagged via `activity`) on re-import, so re-running the import updates entries in place.
    @Query("SELECT s FROM CasualWorkSession s LEFT JOIN FETCH s.entries e LEFT JOIN FETCH e.employee " +
           "WHERE s.farm.id = :farmId AND s.sessionDate = :sessionDate AND s.activity = :activity")
    Optional<CasualWorkSession> findByFarmIdAndSessionDateAndActivity(
            @Param("farmId") Integer farmId, @Param("sessionDate") LocalDate sessionDate, @Param("activity") String activity);
}
