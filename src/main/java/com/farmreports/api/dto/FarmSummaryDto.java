package com.farmreports.api.dto;

import java.time.LocalDateTime;

public record FarmSummaryDto(
    Integer farmId,
    String farmName,
    LocalDateTime lastSubmittedAt,
    long reportsThisYear,
    double totalMilkThisMonth,
    double totalExpensesThisMonth
) {}
