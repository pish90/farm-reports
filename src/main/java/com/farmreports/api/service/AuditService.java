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
        Page<AuditLog> results = auditLogRepository.findFiltered(
                farmId, userId, actionName, start, end, PageRequest.of(page, size));

        List<AuditLogDto> content = results.getContent().stream().map(this::toDto).toList();
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

    private AuditLogDto toDto(AuditLog a) {
        return new AuditLogDto(
                a.getId(), a.getTimestamp(),
                a.getUserId(), a.getUserName(), a.getUserRole(),
                a.getFarmId(), a.getFarmName(),
                a.getAction(),
                a.getEntityType(), a.getEntityId(),
                a.getDescription(), a.getIpAddress()
        );
    }
}
