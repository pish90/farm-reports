package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CasualLabourerPaymentDto(
        Integer id,
        Integer casualLabourerId,
        String labourerName,
        LocalDate paymentDate,
        BigDecimal amount,
        String note,
        String paidBy,
        LocalDateTime createdAt
) {}
