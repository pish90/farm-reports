package com.farmreports.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WorkSessionEntryRequest(
        @NotNull Integer casualLabourerId,
        BigDecimal rateOverride
) {}
