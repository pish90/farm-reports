package com.farmreports.api.dto;

import java.math.BigDecimal;

public record MilkRecordDto(Integer id, Integer dayOfMonth, BigDecimal litres) {}
