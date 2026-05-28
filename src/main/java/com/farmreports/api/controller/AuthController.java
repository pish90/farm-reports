package com.farmreports.api.controller;

import com.farmreports.api.dto.AuthResponse;
import com.farmreports.api.dto.ChangePasswordRequest;
import com.farmreports.api.dto.LoginRequest;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.entity.User;
import com.farmreports.api.repository.UserRepository;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.security.JwtService;
import com.farmreports.api.service.AuditService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            auditService.logAnonymous(AuditAction.LOGIN_FAILED, request.email(),
                    "Failed login attempt for: " + request.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Integer farmId   = user.getFarm() != null ? user.getFarm().getId()   : null;
        String  farmName = user.getFarm() != null ? user.getFarm().getName() : null;

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",            user.getId());
        claims.put("userName",          user.getName());
        claims.put("role",              user.getRole().name());
        claims.put("mustChangePassword", user.isMustChangePassword());
        if (farmId   != null) claims.put("farmId",   farmId);
        if (farmName != null) claims.put("farmName", farmName);

        String token = jwtService.generateToken(user.getEmail(), claims);

        auditService.logLogin(AuditAction.LOGIN, user.getId(), user.getName(), user.getRole().name(),
                farmId, farmName, "Successful login");

        return new AuthResponse(token, user.getId(), farmId, farmName, user.getName(), user.isMustChangePassword());
    }

    @Transactional
    @PutMapping("/password")
    public AuthResponse changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        Claims claims = (Claims) authentication.getPrincipal();
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);

        auditService.log(AuditAction.PASSWORD_CHANGED, authentication,
                ClaimsHelper.getFarmId(authentication), ClaimsHelper.getFarmName(authentication),
                "User", String.valueOf(user.getId()), "Password changed");

        Integer farmId   = user.getFarm() != null ? user.getFarm().getId()   : null;
        String  farmName = user.getFarm() != null ? user.getFarm().getName() : null;

        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("userId",             user.getId());
        newClaims.put("userName",           user.getName());
        newClaims.put("role",               user.getRole().name());
        newClaims.put("mustChangePassword", false);
        if (farmId   != null) newClaims.put("farmId",   farmId);
        if (farmName != null) newClaims.put("farmName", farmName);

        String newToken = jwtService.generateToken(email, newClaims);
        return new AuthResponse(newToken, user.getId(), farmId, farmName, user.getName(), false);
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Claims claims  = (Claims) authentication.getPrincipal();
        Object farmObj = claims.get("farmId");
        Integer farmId = farmObj != null ? ((Number) farmObj).intValue() : null;
        Boolean mustChange = claims.get("mustChangePassword", Boolean.class);
        return new AuthResponse(
                null,
                ((Number) claims.get("userId")).intValue(),
                farmId,
                claims.get("farmName", String.class),
                claims.get("userName", String.class),
                mustChange
        );
    }
}
