package com.farmreports.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseEntryRequest(
        @NotNull Integer entryNo,
        @NotNull LocalDate date,
        String supplierContractor,
        String refNo,
        @NotNull @DecimalMin("0.00") BigDecimal cost
) {}
