package com.farmreports.api.dto;

import java.math.BigDecimal;

public record CasualAttendanceRecordDto(
        Integer id,
        Integer casualLabourerId,
        String casualLabourerName,
        Integer dayOfMonth,
        boolean present,
        String status,
        BigDecimal rateOverride,
        BigDecimal effectiveRate,
        String taskDescription
) {}
