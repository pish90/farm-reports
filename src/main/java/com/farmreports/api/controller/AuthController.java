package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.AuthResponse;
import com.farmreports.api.dto.ChangePasswordRequest;
import com.farmreports.api.dto.LoginRequest;
import com.farmreports.api.entity.User;
import com.farmreports.api.repository.UserRepository;
import com.farmreports.api.security.JwtService;
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

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "farmId", user.getFarm().getId(),
                "farmName", user.getFarm().getName(),
                "userName", user.getName(),
                "role", user.getRole().name()
        );

        String token = jwtService.generateToken(user.getEmail(), claims);

        return new AuthResponse(token, user.getId(), user.getFarm().getId(),
                user.getFarm().getName(), user.getName());
    }

    @Transactional
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(
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
        userRepository.save(user);
        return ApiResponse.ok(null);
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Claims claims = (Claims) authentication.getPrincipal();
        return new AuthResponse(
                null,
                ((Number) claims.get("userId")).intValue(),
                ((Number) claims.get("farmId")).intValue(),
                claims.get("farmName", String.class),
                claims.get("userName", String.class)
        );
    }
}
