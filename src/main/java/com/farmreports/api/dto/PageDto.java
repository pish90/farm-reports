package com.farmreports.api.dto;

import java.util.List;

public record PageDto<T>(List<T> content, long totalElements, int totalPages, int page, int size) {}
