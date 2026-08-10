package com.farmreports.api.dto;

public record ImportRowError(int row, String rowSummary, String message) {}
