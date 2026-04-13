package com.farmreports.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AttendanceEntryRequest(
        @NotNull Integer workerId,
        @NotNull @Min(1) @Max(31) Integer dayOfMonth,
        @NotNull Boolean present,
        String notes
) {}
