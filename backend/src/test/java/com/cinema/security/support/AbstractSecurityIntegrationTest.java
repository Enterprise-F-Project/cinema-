package com.cinema.security.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Shared Spring Boot + manually started Testcontainers PostgreSQL base.
 * Container is started once for the JVM so the datasource stays available
 * for the full security integration suite.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractSecurityIntegrationTest {

    protected static final String DEMO_PASSWORD = "Password123!";
    protected static final String ADMIN_EMAIL = "admin@cinema.com";
    protected static final String DISTRIBUTOR_EMAIL = "distributor@cinema.com";
    protected static final String CLIENT_EMAIL = "client@cinema.com";
    protected static final String JWT_SECRET = "change-me-in-production-use-a-long-random-secret-key";

    static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("cinema_db")
                .withUsername("cinema_user")
                .withPassword("cinema_pass");
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.jwt.secret", () -> JWT_SECRET);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String loginAndGetToken(String email, String password) throws Exception {
        String body = """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }
}
