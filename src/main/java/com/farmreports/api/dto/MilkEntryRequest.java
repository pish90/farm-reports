package com.farmreports.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MilkEntryRequest(
        @NotNull @Min(1) @Max(31) Integer dayOfMonth,
        @NotNull @DecimalMin("0.00") BigDecimal litres
) {}
