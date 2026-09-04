package com.farmreports.api.dto;

import java.util.List;

public record EmployeeCsvImportResult(
        boolean success,
        int totalRows,
        int importedCount,
        int mergedCount,
        List<EmployeeCsvRowError> errors
) {}
