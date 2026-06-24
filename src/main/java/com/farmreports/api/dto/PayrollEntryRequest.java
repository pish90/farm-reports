package com.farmreports.api.dto;

import java.math.BigDecimal;

public record PayrollEntryRequest(
        Integer employeeId,
        BigDecimal salaryRate,
        Integer daysWorked,
        BigDecimal grossSalary,
        BigDecimal loans,
        BigDecimal amountPaid,
        BigDecimal amountRemaining,
        String notes
) {}
