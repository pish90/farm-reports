package com.farmreports.api.dto;

public record AttendanceRecordDto(
        Integer id,
        Integer workerId,
        String workerName,
        Integer dayOfMonth,
        boolean present,
        String notes
) {}
