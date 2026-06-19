package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CasualLabourerDto(
        Integer id,
        String employeeId,
        String firstName,
        String lastName,
        String name,
        String phone,
        String photoBase64,
        String photoMimeType,
        String jobTitle,
        String departmentName
) {}
