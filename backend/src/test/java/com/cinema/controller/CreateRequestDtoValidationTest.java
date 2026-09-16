package com.cinema.controller;

import com.cinema.exception.GlobalExceptionHandler;
import com.cinema.security.JwtAuthenticationFilter;
import com.cinema.security.JwtService;
import com.cinema.service.LicenseService;
import com.cinema.service.MovieService;
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

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Phase 6 — Create-request DTO Bean Validation via MockMvc.
 * Demonstrates BVA on {@code CreateMovieRequest.duration} and EP/null validation on
 * {@code CreateLicenseRequest.movieId}.
 */
@WebMvcTest(controllers = {MovieController.class, LicenseController.class})
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class CreateRequestDtoValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private LicenseService licenseService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("BVA: movie duration 0 (invalid; @Positive requires > 0) → 400")
    void createMovie_withZeroDuration_shouldReturnBadRequest() throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", "Validation Movie");
        payload.put("genre", "Drama");
        payload.put("releaseDate", "2026-01-01");
        payload.put("duration", 0);
        payload.put("description", "Validation test movie");

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'duration')]").exists());

        verify(movieService, never()).create(any());
    }

    @Test
    @DisplayName("EP: null movieId (invalid null-input class) → 400")
    void createLicense_withNullMovieId_shouldReturnBadRequest() throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("startDate", "2026-01-01");
        payload.put("endDate", "2026-12-31");
        payload.put("terms", "Validation test");
        payload.put("movieId", null);
        payload.put("clientId", 1);

        mockMvc.perform(post("/api/licenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'movieId')]").exists());

        verify(licenseService, never()).create(any());
    }
}
