package com.esieeit.projetsi.api.controller;

import com.esieeit.projetsi.application.service.JwtService;
import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.model.User;
import com.esieeit.projetsi.infrastructure.repository.UserRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:securitydb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "jwt.secret=c2VjdXJpdHktdGVzdC1qd3Qta2V5LW11c3QtYmUtbG9uZy1lbm91Z2gtZm9yLWpqd3QtdmFsaWRhdGlvbi0xMjM0NQ==",
        "jwt.expiration-ms=3600000"
})
class SecurityTestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User("user@example.com", "simpleuser", Set.of(UserRole.USER));
        user.setPasswordHash(passwordEncoder.encode("MotDePasse123!"));
        user = userRepository.save(user);
        userToken = jwtService.generateToken(user);

        User admin = new User("admin@example.com", "superadmin", Set.of(UserRole.ADMIN));
        admin.setPasswordHash(passwordEncoder.encode("MotDePasse123!"));
        admin = userRepository.save(admin);
        adminToken = jwtService.generateToken(admin);
    }

    @Test
    void shouldAllowPublicRouteWithoutToken() throws Exception {
        mockMvc.perform(get("/api/security-test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access").value("public"));
    }

    @Test
    void shouldRequireTokenForProtectedRoutes() throws Exception {
        mockMvc.perform(get("/api/security-test/common"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void shouldAllowAuthenticatedUserOnCommonAndUserRoutes() throws Exception {
        mockMvc.perform(get("/api/security-test/common")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("simpleuser"));

        mockMvc.perform(get("/api/security-test/user")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access").value("user"));

        mockMvc.perform(get("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUserOnAdminRouteButAllowAdmin() throws Exception {
        mockMvc.perform(get("/api/security-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));

        mockMvc.perform(get("/api/security-test/admin")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("superadmin"));
    }

    @Test
    void shouldRejectInvalidBearerToken() throws Exception {
        mockMvc.perform(get("/api/security-test/common")
                        .header(HttpHeaders.AUTHORIZATION, bearer("invalid.token.value")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
