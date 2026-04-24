package com.farmreports.api.dto;

public record FarmLiveStatusDto(
        int farmId,
        String farmName,
        int year,
        int month,
        String reportStatus,       // "NOT_STARTED" | "DRAFT" | "SUBMITTED"
        Integer reportId,
        int activeWorkers,
        long attendanceDaysRecorded,
        double milkTotalLitres,
        long expenseCount,
        double expenseTotal,
        boolean livestockEntered
) {}
