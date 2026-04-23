package com.farmreports.api.dto;

public record ReceiptScanResult(
        Integer day,
        String supplierContractor,
        String receiptNo,
        Double cost,
        String description
) {}
