package com.farmreports.api.dto;

public record LivestockSummaryDto(Integer farmId, Integer year, Integer month, String category, String type, Integer totalCount) {}
