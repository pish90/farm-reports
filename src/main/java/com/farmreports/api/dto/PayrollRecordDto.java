package com.farmreports.api.dto;

import java.math.BigDecimal;

public record PayrollRecordDto(
        Integer id,
        Integer employeeId,
        String employeeName,
        String employeeCode,
        String lsNumber,
        BigDecimal salaryRate,
        Integer daysWorked,
        BigDecimal grossSalary,
        BigDecimal loans,
        BigDecimal amountPaid,
        BigDecimal amountRemaining,
        String notes
) {}
