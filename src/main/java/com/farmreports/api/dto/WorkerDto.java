package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WorkerDto(
        Integer id,
        String employeeId,
        String firstName,
        String lastName,
        String name,
        String phone,
        String jobTitle,
        String departmentName,
        String startDate,
        String status
) {}
