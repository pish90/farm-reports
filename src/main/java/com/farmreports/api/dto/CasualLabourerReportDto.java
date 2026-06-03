package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CasualLabourerReportDto(
        Integer labourerId,
        String name,
        String phone,
        String photoBase64,
        String photoMimeType,
        BigDecimal allTimeEarned,
        BigDecimal allTimePaid,
        BigDecimal balance,
        List<WorkEntryLine> workEntries
) {
    public record WorkEntryLine(
            Integer sessionId,
            LocalDate sessionDate,
            String activity,
            BigDecimal amount
    ) {}
}
