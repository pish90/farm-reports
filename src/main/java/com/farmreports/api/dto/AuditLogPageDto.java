package com.farmreports.api.dto;

import java.util.List;

public record AuditLogPageDto(
        List<AuditLogDto> content,
        long totalElements,
        int totalPages,
        int page,
        int size
) {}
