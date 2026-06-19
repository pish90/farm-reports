package com.farmreports.api.dto;

import jakarta.validation.constraints.Size;

public record CasualLabourerRequest(
        // New preferred fields
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 50)  String phone,
        @Size(max = 100) String jobTitle,
        Integer departmentId,
        String startDate,
        String photoBase64,
        String photoMimeType,
        // Legacy field: accepted if firstName is absent (mobile backward compat)
        @Size(max = 255) String name
) {}
