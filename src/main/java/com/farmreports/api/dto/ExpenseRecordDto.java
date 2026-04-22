package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseRecordDto(
        Integer id,
        Integer entryNo,
        LocalDate date,
        String supplierContractor,
        String receiptNo,
        BigDecimal cost,
        String description,
        Integer categoryId,
        String categoryCode,
        String categoryName,
        Integer businessUnitId,
        String businessUnitCode,
        String businessUnitName,
        List<ApportionmentDto> apportionments
) {
    public record ApportionmentDto(
            Integer businessUnitId,
            String businessUnitCode,
            String businessUnitName,
            BigDecimal percentage,
            BigDecimal amount
    ) {}
}
