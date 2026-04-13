package com.farmreports.api.dto;

public record LivestockRecordDto(
        Integer id,
        Integer livestockTypeId,
        String category,
        String type,
        Integer count
) {}
