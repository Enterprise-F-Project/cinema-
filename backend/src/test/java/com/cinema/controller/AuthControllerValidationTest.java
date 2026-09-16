package com.cinema.controller;

import com.cinema.dto.response.UserResponse;
import com.cinema.exception.GlobalExceptionHandler;
import com.cinema.model.Role;
import com.cinema.security.JwtAuthenticationFilter;
import com.cinema.security.JwtService;
import com.cinema.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Phase 6 — Auth registration Bean Validation via MockMvc.
 * Demonstrates Boundary Value Analysis and Equivalence Partitioning on {@code RegisterRequest}.
 */
@WebMvcTest(controllers = AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("BVA: password length 7 (below min 8) → 400")
    void register_withPasswordBelowMinimum_shouldReturnBadRequest() throws Exception {
        String body = objectMapper.writeValueAsString(registerPayload("validation7@example.com", "Pass123"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password')]").exists());

        verify(authService, never()).register(any());
    }

    @Test
    @DisplayName("EP: invalid email equivalence class → 400")
    void register_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        Map<String, Object> payload = registerPayload("not-an-email", "Password123!");
        String body = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'email')]").exists());

        verify(authService, never()).register(any());
    }

    @Test
    @DisplayName("BVA: password length exactly 8 (min boundary) → 201")
    void register_withPasswordAtMinimumBoundary_shouldBeAccepted() throws Exception {
        when(authService.register(any())).thenReturn(new UserResponse(
                99L,
                "Validation Test User",
                "validation8@example.com",
                Role.CLIENT,
                true,
                LocalDateTime.now()));

        // Exactly 8 characters — minimum valid boundary for @Size(min = 8)
        String password = "Pass1234";
        assert password.length() == 8;

        String body = objectMapper.writeValueAsString(registerPayload("validation8@example.com", password));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("validation8@example.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));

        verify(authService).register(any());
    }

    private static Map<String, Object> registerPayload(String email, String password) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("fullName", "Validation Test User");
        payload.put("email", email);
        payload.put("password", password);
        payload.put("role", "CLIENT");
        return payload;
    }
}
