package com.farmreports.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmreports.api.config.SecurityConfig;
import com.farmreports.api.dto.LoginRequest;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.entity.User;
import com.farmreports.api.entity.UserRole;
import com.farmreports.api.repository.UserRepository;
import com.farmreports.api.security.JwtAuthFilter;
import com.farmreports.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "jwt.secret=test-secret-that-is-at-least-32-bytes-long-for-hmac",
        "jwt.expiration-ms=86400000"
})
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Farm farm = new Farm();
        farm.setId(1);
        farm.setName("Test Farm");

        testUser = new User();
        testUser.setId(42);
        testUser.setEmail("farmer@example.com");
        testUser.setName("John Farmer");
        testUser.setPasswordHash(passwordEncoder.encode("secret123"));
        testUser.setRole(UserRole.ADMIN);
        testUser.setFarm(farm);
    }

    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        when(userRepository.findByEmail("farmer@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("farmer@example.com", "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.farmId").value(1))
                .andExpect(jsonPath("$.farmName").value("Test Farm"))
                .andExpect(jsonPath("$.userName").value("John Farmer"));
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        when(userRepository.findByEmail("farmer@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("farmer@example.com", "wrongpassword"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withUnknownEmail_returns401() throws Exception {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("nobody@example.com", "secret123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withValidToken_returnsUserDetails() throws Exception {
        when(userRepository.findByEmail("farmer@example.com")).thenReturn(Optional.of(testUser));

        // Obtain a token via login first
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("farmer@example.com", "secret123"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.farmId").value(1))
                .andExpect(jsonPath("$.farmName").value("Test Farm"))
                .andExpect(jsonPath("$.userName").value("John Farmer"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void me_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
