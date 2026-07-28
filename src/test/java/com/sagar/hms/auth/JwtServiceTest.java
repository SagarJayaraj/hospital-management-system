package com.sagar.hms.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void shouldGenerateAndValidateToken() {
        JwtService jwtService = new JwtService("very-secret-key", "hms", 3600);
        User user = new User("admin", "pass", java.util.List.of());

        String token = jwtService.generateToken(user);

        assertEquals("admin", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldRejectTokenWithWrongSecret() {
        JwtService issuerService = new JwtService("very-secret-key", "hms", 3600);
        JwtService validatorService = new JwtService("another-secret-key", "hms", 3600);
        User user = new User("admin", "pass", java.util.List.of());
        String token = issuerService.generateToken(user);

        assertThrows(Exception.class, () -> validatorService.extractUsername(token));
    }
}
