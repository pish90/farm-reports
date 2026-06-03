package com.farmreports.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CasualLabourerRequest(
        @NotBlank String name,
        String phone,
        String photoBase64,
        String photoMimeType
) {}
