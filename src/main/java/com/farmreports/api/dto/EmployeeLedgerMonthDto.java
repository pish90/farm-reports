package com.farmreports.api.dto;

import java.math.BigDecimal;

public record EmployeeLedgerMonthDto(
        Integer month,
        BigDecimal earned,
        BigDecimal paid,
        BigDecimal balance
) {}
