package com.farmreports.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkerRequest(
        @NotBlank @Size(max = 255) String name
) {}
