package com.farmreports.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CasualLabourerRequest(
        @NotBlank String name,
        String phone,
        @NotNull BigDecimal defaultDailyRate,
        String photoBase64,
        String photoMimeType
) {}
