package com.farmreports.api.service;

import com.farmreports.api.dto.AuditLogDto;
import com.farmreports.api.dto.AuditLogPageDto;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.entity.AuditLog;
import com.farmreports.api.repository.AuditLogRepository;
import com.farmreports.api.security.ClaimsHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /** Log an action performed by an authenticated user. */
    public void log(AuditAction action, Authentication auth,
                    Integer farmId, String farmName,
                    String entityType, String entityId, String description) {
        Integer userId   = null;
        String  userName = null;
        String  userRole = null;
        if (auth != null) {
            userId   = ClaimsHelper.getUserId(auth);
            userName = ClaimsHelper.getUserName(auth);
            userRole = ClaimsHelper.getRole(auth);
        }
        persist(action, userId, userName, userRole, farmId, farmName, entityType, entityId, description);
    }

    /** Log a login event where we have user info but no JWT Authentication yet. */
    public void logLogin(AuditAction action, Integer userId, String userName, String userRole,
                         Integer farmId, String farmName, String description) {
        persist(action, userId, userName, userRole, farmId, farmName, "User",
                userId != null ? String.valueOf(userId) : null, description);
    }

    /** Log a failed login or other anonymous event where we only have an email. */
    public void logAnonymous(AuditAction action, String attemptedEmail, String description) {
        persist(action, null, attemptedEmail, null, null, null, null, null, description);
    }

    @Transactional(readOnly = true)
    public AuditLogPageDto getAuditLogs(Integer farmId, Integer userId, String action,
                                        LocalDate startDate, LocalDate endDate,
                                        int page, int size) {
        AuditAction auditAction = null;
        if (action != null && !action.isBlank()) {
            try {
                auditAction = AuditAction.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                // unknown action — return empty result
                return new AuditLogPageDto(List.of(), 0, 0, page, size);
            }
        }

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end   = endDate   != null ? endDate.atTime(23, 59, 59) : null;

        String actionName = auditAction != null ? auditAction.name() : null;
        Page<Object[]> results = auditLogRepository.findFiltered(
                farmId, userId, actionName, start, end, PageRequest.of(page, size));

        List<AuditLogDto> content = results.getContent().stream().map(this::rowToDto).toList();
        return new AuditLogPageDto(content, results.getTotalElements(),
                results.getTotalPages(), page, size);
    }

    // ── internals ─────────────────────────────────────────────────────────────

    private void persist(AuditAction action, Integer userId, String userName, String userRole,
                         Integer farmId, String farmName,
                         String entityType, String entityId, String description) {
        try {
            AuditLog entry = new AuditLog();
            entry.setAction(action);
            entry.setUserId(userId);
            entry.setUserName(userName);
            entry.setUserRole(userRole);
            entry.setFarmId(farmId);
            entry.setFarmName(farmName);
            entry.setEntityType(entityType);
            entry.setEntityId(entityId);
            entry.setDescription(description);
            entry.setIpAddress(resolveIp());
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to write audit log [action={}]: {}", action, e.getMessage());
        }
    }

    private String resolveIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }

    // Column order matches the SELECT in AuditLogRepository.findFiltered:
    // 0:id  1:timestamp  2:user_id  3:user_name  4:user_role
    // 5:farm_id  6:farm_name  7:action  8:entity_type  9:entity_id  10:description  11:ip_address
    private AuditLogDto rowToDto(Object[] row) {
        Long id = row[0] != null ? ((Number) row[0]).longValue() : null;

        LocalDateTime timestamp = null;
        if (row[1] instanceof Timestamp ts) {
            timestamp = ts.toLocalDateTime();
        } else if (row[1] instanceof LocalDateTime ldt) {
            timestamp = ldt;
        }

        Integer userId   = row[2] != null ? ((Number) row[2]).intValue() : null;
        String  userName = (String) row[3];
        String  userRole = (String) row[4];
        Integer farmId   = row[5] != null ? ((Number) row[5]).intValue() : null;
        String  farmName = (String) row[6];

        AuditAction action = null;
        if (row[7] instanceof String s) {
            try { action = AuditAction.valueOf(s); } catch (IllegalArgumentException ignored) {}
        }

        String entityType  = (String) row[8];
        String entityId    = (String) row[9];
        String description = (String) row[10];
        String ipAddress   = (String) row[11];

        return new AuditLogDto(id, timestamp, userId, userName, userRole,
                farmId, farmName, action, entityType, entityId, description, ipAddress);
    }
}
