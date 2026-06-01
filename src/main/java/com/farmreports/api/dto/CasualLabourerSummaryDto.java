package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record CasualLabourerSummaryDto(
        BigDecimal allTimeEarned,
        BigDecimal allTimePaid,
        BigDecimal outstanding,
        List<CasualLabourerPaymentDto> payments
) {}
