package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmployeeDto(
        Integer id,
        String lsNumber,
        String employeeId,
        String firstName,
        String lastName,
        String fullName,
        String phone,
        String employmentType,
        String jobTitle,
        String departmentName,
        String startDate,
        String dateOfBirth,
        String nationalId,
        String gender,
        Integer age,
        String status,
        BigDecimal defaultDailyRate,
        String photoBase64,
        String photoMimeType
) {}
