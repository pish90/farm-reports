package com.farmreports.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CasualAttendanceEntryRequest(
        @NotNull Integer casualLabourerId,
        @NotNull Integer dayOfMonth,
        @NotNull Boolean present,
        String status,
        BigDecimal rateOverride,
        String taskDescription
) {}
