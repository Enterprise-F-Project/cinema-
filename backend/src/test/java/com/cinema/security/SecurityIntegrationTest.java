package com.cinema.security;

import com.cinema.model.Role;
import com.cinema.security.support.AbstractSecurityIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security integration tests for authentication, JWT handling, and role authorization
 * against the real SecurityFilterChain + @PreAuthorize configuration.
 *
 * <p>Note on unauthenticated responses: with the current Spring Security setup
 * (no custom AuthenticationEntryPoint), anonymous access to protected endpoints
 * returns HTTP 403 rather than 401. Tests assert the application's actual behavior.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class SecurityIntegrationTest extends AbstractSecurityIntegrationTest {

    // --- A. Public access ---

    @Test
    @DisplayName("A1 register is accessible without authentication")
    void register_isAccessibleWithoutAuthentication() throws Exception {
        String email = "security-register-" + UUID.randomUUID() + "@cinema.com";
        String body = """
                {
                  "fullName": "Security Register Client",
                  "email": "%s",
                  "password": "Password123!",
                  "role": "CLIENT"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    @Test
    @DisplayName("A2 login is accessible without authentication")
    void login_isAccessibleWithoutAuthentication() throws Exception {
        String body = """
                {"email":"%s","password":"%s"}
                """.formatted(CLIENT_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    // --- B. Authentication / JWT ---

    @Test
    @DisplayName("B1 protected endpoint without JWT is rejected")
    void protectedEndpoint_withoutJwt_isRejected() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("B2 protected endpoint with malformed JWT is rejected")
    void protectedEndpoint_withMalformedJwt_isRejected() throws Exception {
        mockMvc.perform(get("/api/movies")
                        .header("Authorization", "Bearer not-a-real-jwt"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("B3 valid CLIENT JWT can access authenticated movie list")
    void validClientJwt_allowsAccessToMovies() throws Exception {
        String token = loginAndGetToken(CLIENT_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(get("/api/movies")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("B4 login with wrong password returns 401")
    void login_withInvalidCredentials_returnsUnauthorized() throws Exception {
        String body = """
                {"email":"%s","password":"WrongPassword!"}
                """.formatted(CLIENT_EMAIL);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // --- C. Authorization / roles ---

    @Test
    @DisplayName("C1 CLIENT accessing ADMIN dashboard returns 403")
    void client_accessingAdminEndpoint_returnsForbidden() throws Exception {
        String clientToken = loginAndGetToken(CLIENT_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", bearer(clientToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @DisplayName("C2 ADMIN can access ADMIN dashboard")
    void admin_accessingAdminEndpoint_succeeds() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").exists());
    }

    @Test
    @DisplayName("C3 DISTRIBUTOR creating rental returns 403")
    void distributor_creatingRental_returnsForbidden() throws Exception {
        String distributorToken = loginAndGetToken(DISTRIBUTOR_EMAIL, DEMO_PASSWORD);
        String body = """
                {
                  "movieId": 1,
                  "rentalDate": "2026-03-01T10:00:00",
                  "returnDate": "2026-03-08T10:00:00"
                }
                """;

        mockMvc.perform(post("/api/rentals")
                        .header("Authorization", bearer(distributorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @DisplayName("C4 CLIENT creating movie returns 403")
    void client_creatingMovie_returnsForbidden() throws Exception {
        String clientToken = loginAndGetToken(CLIENT_EMAIL, DEMO_PASSWORD);
        String body = """
                {
                  "title": "Unauthorized Client Film",
                  "genre": "Drama",
                  "releaseDate": "2024-01-15",
                  "duration": 120,
                  "description": "Should be forbidden"
                }
                """;

        mockMvc.perform(post("/api/movies")
                        .header("Authorization", bearer(clientToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @DisplayName("C5 DISTRIBUTOR can create a movie")
    void distributor_creatingMovie_succeeds() throws Exception {
        String distributorToken = loginAndGetToken(DISTRIBUTOR_EMAIL, DEMO_PASSWORD);
        String body = """
                {
                  "title": "Authorized Distributor Film",
                  "genre": "Drama",
                  "releaseDate": "2024-06-01",
                  "duration": 110,
                  "description": "Created by distributor in security test"
                }
                """;

        mockMvc.perform(post("/api/movies")
                        .header("Authorization", bearer(distributorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Authorized Distributor Film"))
                .andExpect(jsonPath("$.availabilityStatus").value("AVAILABLE"));
    }

    @Test
    @DisplayName("C6 CLIENT can list rentals")
    void client_listingRentals_succeeds() throws Exception {
        String clientToken = loginAndGetToken(CLIENT_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(get("/api/rentals")
                        .header("Authorization", bearer(clientToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("C7 CLIENT accessing licenses returns 403")
    void client_accessingLicenses_returnsForbidden() throws Exception {
        String clientToken = loginAndGetToken(CLIENT_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(get("/api/licenses")
                        .header("Authorization", bearer(clientToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @DisplayName("C8 DISTRIBUTOR can list licenses")
    void distributor_listingLicenses_succeeds() throws Exception {
        String distributorToken = loginAndGetToken(DISTRIBUTOR_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(get("/api/licenses")
                        .header("Authorization", bearer(distributorToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("C9 CLIENT accessing clients returns 403")
    void client_accessingClients_returnsForbidden() throws Exception {
        String clientToken = loginAndGetToken(CLIENT_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(get("/api/clients")
                        .header("Authorization", bearer(clientToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    // --- D. JWT / account security ---

    @Test
    @DisplayName("D1 disabled account cannot login")
    void login_whenAccountDisabled_isRejected() throws Exception {
        // Spring Security rejects disabled UserDetails during authenticate()
        // before AuthService's explicit "Account is disabled" check runs.
        String email = "disabled-security-" + UUID.randomUUID() + "@cinema.com";
        String registerBody = """
                {
                  "fullName": "Soon Disabled",
                  "email": "%s",
                  "password": "Password123!",
                  "role": "CLIENT"
                }
                """.formatted(email);

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn();

        long userId = objectMapper.readTree(registerResult.getResponse().getContentAsString())
                .get("id").asLong();

        String adminToken = loginAndGetToken(ADMIN_EMAIL, DEMO_PASSWORD);
        mockMvc.perform(patch("/api/admin/users/" + userId + "/status")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk());

        String loginBody = """
                {"email":"%s","password":"Password123!"}
                """.formatted(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("D2 blacklisted JWT after logout is rejected")
    void logout_blacklistsToken_subsequentRequestRejected() throws Exception {
        String token = loginAndGetToken(CLIENT_EMAIL, DEMO_PASSWORD);

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        mockMvc.perform(get("/api/movies")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("D3 expired JWT is rejected")
    void expiredJwt_isRejected() throws Exception {
        String expiredToken = craftExpiredToken(CLIENT_EMAIL, Role.CLIENT);

        mockMvc.perform(get("/api/movies")
                        .header("Authorization", bearer(expiredToken)))
                .andExpect(status().isForbidden());
    }

    // --- E. Security policy gap (documented, not fixed) ---

    @Test
    @DisplayName("E1 SECURITY FINDING: public registration currently allows ADMIN role")
    void register_withAdminRole_isCurrentlyAllowed() throws Exception {
        String email = "self-admin-" + UUID.randomUUID() + "@cinema.com";
        String body = """
                {
                  "fullName": "Self Registered Admin",
                  "email": "%s",
                  "password": "Password123!",
                  "role": "ADMIN"
                }
                """.formatted(email);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("role").asText()).isEqualTo("ADMIN");
        assertThat(json.get("active").asBoolean()).isTrue();
    }

    private String craftExpiredToken(String email, Role role) {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(Date.from(now.minus(2, ChronoUnit.HOURS)))
                .expiration(Date.from(now.minus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }
}
