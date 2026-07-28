package com.sagar.hms.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final String issuer;
    private final long expirySeconds;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.issuer:hospital-management-system}") String issuer,
                      @Value("${security.jwt.expiry-seconds:3600}") long expirySeconds) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expirySeconds = expirySeconds;
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(expirySeconds))
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
}
