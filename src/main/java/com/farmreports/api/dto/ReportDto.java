package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReportDto(
        Integer id,
        Integer farmId,
        Integer year,
        Integer month,
        String status,
        LocalDateTime submittedAt,
        LocalDateTime createdAt,
        List<AttendanceRecordDto> attendance,
        List<LivestockRecordDto> livestock,
        List<MilkRecordDto> milk,
        List<ExpenseRecordDto> expenses
) {}
