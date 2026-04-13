package com.farmreports.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String token,
        Integer userId,
        Integer farmId,
        String farmName,
        String userName
) {}
