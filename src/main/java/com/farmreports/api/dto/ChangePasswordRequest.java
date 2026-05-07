package com.farmreports.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._#^])[A-Za-z\\d@$!%*?&._#^]{8,}$",
            message = "Password must be at least 8 characters and include uppercase, lowercase, number, and symbol (@$!%*?&._#^)"
        )
        String newPassword
) {}
