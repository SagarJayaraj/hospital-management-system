package com.sagar.hms.auth;

import com.sagar.hms.exception.BadRequestException;
import com.sagar.hms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }

        AppUser saved = appUserRepository.save(AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build());

        return new AuthDtos.AuthResponse(jwtService.generateToken(saved));
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        AppUser user = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.username()));
        return new AuthDtos.AuthResponse(jwtService.generateToken(user));
    }
}
