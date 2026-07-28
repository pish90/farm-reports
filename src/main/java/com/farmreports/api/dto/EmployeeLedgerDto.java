package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record EmployeeLedgerDto(
        Integer year,
        BigDecimal openingBalance,
        BigDecimal totalEarned,
        BigDecimal totalPaid,
        BigDecimal closingBalance,
        List<EmployeeLedgerMonthDto> months
) {}
