package com.farmreports.api.dto;

import com.farmreports.api.entity.AuditAction;

import java.time.LocalDateTime;

public record AuditLogDto(
        Long id,
        LocalDateTime timestamp,
        Integer userId,
        String userName,
        String userRole,
        Integer farmId,
        String farmName,
        AuditAction action,
        String entityType,
        String entityId,
        String description,
        String ipAddress
) {}
