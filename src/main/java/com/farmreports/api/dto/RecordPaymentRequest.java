package com.farmreports.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordPaymentRequest(
        @NotNull LocalDate paymentDate,
        @NotNull @Positive BigDecimal amount,
        String note
) {}
