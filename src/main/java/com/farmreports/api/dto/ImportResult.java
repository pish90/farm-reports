package com.farmreports.api.dto;

import java.util.List;

public record ImportResult(
        boolean success,
        int totalRows,
        int importedCount,
        List<ImportRowError> errors
) {}
