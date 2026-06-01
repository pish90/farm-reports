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
            SELECT id, timestamp, user_id, user_name, user_role,
                   farm_id, farm_name, action,
                   entity_type, entity_id, description, ip_address
            FROM audit_logs
            WHERE (CAST(:farmId    AS INTEGER)   IS NULL OR farm_id   = :farmId)
              AND (CAST(:userId    AS INTEGER)   IS NULL OR user_id   = :userId)
              AND (CAST(:action    AS VARCHAR)   IS NULL OR action    = :action)
              AND (CAST(:startDate AS TIMESTAMP) IS NULL OR timestamp >= :startDate)
              AND (CAST(:endDate   AS TIMESTAMP) IS NULL OR timestamp <= :endDate)
            ORDER BY timestamp DESC
            """,
        countQuery = """
            SELECT COUNT(*) FROM audit_logs
            WHERE (CAST(:farmId    AS INTEGER)   IS NULL OR farm_id   = :farmId)
              AND (CAST(:userId    AS INTEGER)   IS NULL OR user_id   = :userId)
              AND (CAST(:action    AS VARCHAR)   IS NULL OR action    = :action)
              AND (CAST(:startDate AS TIMESTAMP) IS NULL OR timestamp >= :startDate)
              AND (CAST(:endDate   AS TIMESTAMP) IS NULL OR timestamp <= :endDate)
            """,
        nativeQuery = true
    )
    Page<Object[]> findFiltered(
            @Param("farmId")    Integer farmId,
            @Param("userId")    Integer userId,
            @Param("action")    String action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate")   LocalDateTime endDate,
            Pageable pageable
    );
}
