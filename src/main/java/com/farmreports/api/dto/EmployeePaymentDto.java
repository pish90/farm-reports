package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeePaymentDto(
        Integer id,
        Integer employeeId,
        String employeeName,
        LocalDate paymentDate,
        BigDecimal amount,
        String note,
        String paidBy,
        LocalDateTime createdAt
) {}
