package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record EmployeeSummaryDto(
        BigDecimal allTimeEarned,
        BigDecimal allTimePaid,
        BigDecimal outstanding,
        List<EmployeePaymentDto> payments
) {}
