package com.farmreports.api.dto;

import java.math.BigDecimal;

public record MilkSummaryDto(Integer farmId, Integer year, Integer month, BigDecimal totalLitres) {}
