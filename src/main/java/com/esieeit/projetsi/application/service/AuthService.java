package com.esieeit.projetsi.application.service;

import com.esieeit.projetsi.api.dto.auth.AuthResponseDTO;
import com.esieeit.projetsi.api.dto.auth.LoginRequestDTO;
import com.esieeit.projetsi.api.dto.auth.RegisterRequestDTO;
import com.esieeit.projetsi.domain.enums.UserRole;
import com.esieeit.projetsi.domain.exception.InvalidDataException;
import com.esieeit.projetsi.domain.model.User;
import com.esieeit.projetsi.infrastructure.security.AuthenticatedUserDetails;
import com.esieeit.projetsi.infrastructure.repository.UserRepository;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedLogin, request.getPassword()));
            AuthenticatedUserDetails principal = (AuthenticatedUserDetails) authentication.getPrincipal();
            return buildAuthResponse(principal);
        } catch (BadCredentialsException ex) {
            throw InvalidDataException.unauthorized("Identifiants invalides");
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw InvalidDataException.unauthorized("Identifiants invalides");
        }
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

    private AuthResponseDTO buildAuthResponse(AuthenticatedUserDetails principal) {
        return new AuthResponseDTO(
                jwtService.generateToken(principal.getUsername(), principal.getEmail(), principal.getRoles()),
                "Bearer",
                jwtService.getExpirationMs(),
                principal.getId(),
                principal.getUsername(),
                principal.getEmail(),
                principal.getRoles().stream()
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
