package com.farmreports.api.repository;

import com.farmreports.api.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(
        value = """
            SELECT * FROM audit_logs
            WHERE (:farmId IS NULL OR farm_id = :farmId)
              AND (:userId  IS NULL OR user_id  = :userId)
              AND (:action  IS NULL OR action   = :action)
              AND (:startDate IS NULL OR timestamp >= :startDate)
              AND (:endDate   IS NULL OR timestamp <= :endDate)
            ORDER BY timestamp DESC
            """,
        countQuery = """
            SELECT COUNT(*) FROM audit_logs
            WHERE (:farmId IS NULL OR farm_id = :farmId)
              AND (:userId  IS NULL OR user_id  = :userId)
              AND (:action  IS NULL OR action   = :action)
              AND (:startDate IS NULL OR timestamp >= :startDate)
              AND (:endDate   IS NULL OR timestamp <= :endDate)
            """,
        nativeQuery = true
    )
    Page<AuditLog> findFiltered(
            @Param("farmId")    Integer farmId,
            @Param("userId")    Integer userId,
            @Param("action")    String action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate")   LocalDateTime endDate,
            Pageable pageable
    );
}
