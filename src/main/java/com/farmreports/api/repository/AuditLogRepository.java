package com.farmreports.api.repository;

import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:farmId IS NULL OR a.farmId = :farmId)
          AND (:userId IS NULL OR a.userId = :userId)
          AND (:action IS NULL OR a.action = :action)
          AND (:startDate IS NULL OR a.timestamp >= :startDate)
          AND (:endDate IS NULL OR a.timestamp <= :endDate)
        ORDER BY a.timestamp DESC
        """)
    Page<AuditLog> findFiltered(
            @Param("farmId") Integer farmId,
            @Param("userId") Integer userId,
            @Param("action") AuditAction action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
