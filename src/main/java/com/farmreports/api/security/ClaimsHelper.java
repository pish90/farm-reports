package com.farmreports.api.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

public final class ClaimsHelper {

    private ClaimsHelper() {}

    public static Integer getFarmId(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        return ((Number) claims.get("farmId")).intValue();
    }

    public static Integer getUserId(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        return ((Number) claims.get("userId")).intValue();
    }

    public static String getRole(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Object role = claims.get("role");
        return role != null ? (String) role : "WORKER";
    }
}
