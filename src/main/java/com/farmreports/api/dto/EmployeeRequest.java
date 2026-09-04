package com.farmreports.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EmployeeRequest(
        @NotBlank @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 50)  String phone,
        boolean isSalaried,
        boolean isCasual,
        @Size(max = 100) String jobTitle,
        Integer departmentId,
        String startDate,
        String dateOfBirth,
        @Size(max = 50) String nationalId,
        @Size(max = 10) String gender,
        BigDecimal defaultDailyRate,
        String photoBase64,
        String photoMimeType,
        String status
) {}
