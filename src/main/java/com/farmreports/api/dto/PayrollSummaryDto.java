package com.farmreports.api.dto;

import java.math.BigDecimal;

public record PayrollSummaryDto(
        Integer farmId,
        Integer year,
        Integer month,
        BigDecimal totalGross,
        BigDecimal totalLoans,
        BigDecimal totalPaid,
        BigDecimal totalRemaining
) {}
