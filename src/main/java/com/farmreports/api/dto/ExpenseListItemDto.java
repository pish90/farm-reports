package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Flat, farm-tagged expense row for the standalone Expenses page — one row per
 *  expense across farms/months, unlike {@link ExpenseRecordDto} which is scoped
 *  to a single report. */
public record ExpenseListItemDto(
        Integer id,
        Integer reportId,
        Integer farmId,
        String farmName,
        Integer year,
        Integer month,
        LocalDate date,
        String receiptNo,
        String supplierContractor,
        String description,
        String categoryName,
        BigDecimal cost
) {}
