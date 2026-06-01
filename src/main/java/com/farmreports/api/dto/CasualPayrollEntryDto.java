package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CasualPayrollEntryDto(
        Integer labourerId,
        String name,
        String phone,
        String photoBase64,
        String photoMimeType,
        BigDecimal defaultDailyRate,
        Integer daysPresent,
        BigDecimal monthEarnings,
        BigDecimal allTimePaid,
        BigDecimal outstanding
) {}
