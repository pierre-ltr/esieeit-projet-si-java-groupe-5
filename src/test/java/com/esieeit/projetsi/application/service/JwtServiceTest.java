package com.esieeit.projetsi.application.service;

import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.model.User;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "c3VwZXItc2VjcmV0LXRlc3Qta2V5LWZvci1qd3Qtc2VydmljZS1tdXN0LWJlLWxvbmctZW5vdWdoLTEyMzQ1Njc4OTA=";

    @Test
    void shouldGenerateAndValidateTokenForUser() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);
        User user = new User("lydia@example.com", "lydia", Set.of(UserRole.USER));
        user.setId(42L);
        user.setPasswordHash("$2a$10$abcdefghijklmnopqrstuv123456789012345678901234567890");

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("lydia");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
        assertThat(jwtService.extractAllClaims(token).get("email", String.class)).isEqualTo("lydia@example.com");
    }
}
