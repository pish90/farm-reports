package com.farmreports.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseEntryRequest(
        @NotNull Integer entryNo,
        @NotNull LocalDate date,
        String supplierContractor,
        String receiptNo,
        @NotNull @DecimalMin("0.00") BigDecimal cost,
        String description,
        Integer categoryId,
        Integer businessUnitId,
        @Valid List<ApportionmentRequest> apportionments
) {
    public record ApportionmentRequest(
            @NotNull Integer businessUnitId,
            @NotNull @DecimalMin("0.00") BigDecimal percentage,
            @NotNull @DecimalMin("0.00") BigDecimal amount
    ) {}
}
