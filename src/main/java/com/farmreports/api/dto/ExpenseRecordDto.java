package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRecordDto(
        Integer id,
        Integer entryNo,
        LocalDate date,
        String supplierContractor,
        String refNo,
        BigDecimal cost
) {}
