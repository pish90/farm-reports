package com.farmreports.api.dto;

import java.time.LocalDateTime;

public record FarmSummaryDto(
    Integer farmId,
    String farmName,
    LocalDateTime lastReportAt,
    long reportsThisYear,
    double totalMilkThisMonth,
    double totalExpensesThisMonth
) {}
