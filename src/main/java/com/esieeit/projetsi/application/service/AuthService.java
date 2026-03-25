package com.esieeit.projetsi.application.service;

import com.esieeit.projetsi.api.dto.auth.AuthResponseDTO;
import com.esieeit.projetsi.api.dto.auth.LoginRequestDTO;
import com.esieeit.projetsi.api.dto.auth.RegisterRequestDTO;
import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.exception.InvalidDataException;
import com.esieeit.projetsi.domain.model.User;
import com.esieeit.projetsi.infrastructure.repository.UserRepository;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        String normalizedUsername = normalize(request.getUsername());
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new InvalidDataException("username already exists");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new InvalidDataException("email already exists");
        }

        User user = new User(normalizedEmail, normalizedUsername, Set.of(UserRole.USER));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        String normalizedLogin = normalize(request.getLogin());

        User user = resolveByLogin(normalizedLogin)
                .filter(candidate -> passwordEncoder.matches(request.getPassword(), candidate.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));

        return buildAuthResponse(user);
    }

    private java.util.Optional<User> resolveByLogin(String normalizedLogin) {
        if (normalizedLogin.contains("@")) {
            return userRepository.findByEmailIgnoreCase(normalizedLogin);
        }
        return userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(normalizedLogin, normalizedLogin);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        return new AuthResponseDTO(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationMs(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Enum::name)
                        .max(Comparator.naturalOrder())
                        .orElse(UserRole.USER.name()));
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeEmail(String value) {
        return normalize(value);
    }
}
