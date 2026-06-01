package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CasualLabourerDto(
        Integer id,
        String name,
        String phone,
        BigDecimal defaultDailyRate,
        String photoBase64,
        String photoMimeType
) {}
