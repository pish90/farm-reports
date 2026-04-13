package com.farmreports.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LivestockEntryRequest(
        @NotNull Integer livestockTypeId,
        @NotNull @Min(0) Integer count
) {}
