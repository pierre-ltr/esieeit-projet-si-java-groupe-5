package com.esieeit.projetsi.api.controller;

import com.esieeit.projetsi.infrastructure.security.AuthenticatedUserDetails;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security-test")
public class SecurityTestController {

    @GetMapping("/public")
    public Map<String, Object> publicRoute() {
        return Map.of(
                "access", "public",
                "message", "This route is public");
    }

    @GetMapping("/common")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Map<String, Object> commonRoute(@AuthenticationPrincipal AuthenticatedUserDetails principal) {
        return Map.of(
                "access", "authenticated",
                "username", principal.getUsername(),
                "message", "JWT accepted");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public Map<String, Object> userRoute(@AuthenticationPrincipal AuthenticatedUserDetails principal) {
        return Map.of(
                "access", "user",
                "username", principal.getUsername(),
                "message", "User role granted");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> adminRoute(@AuthenticationPrincipal AuthenticatedUserDetails principal) {
        return Map.of(
                "access", "admin",
                "username", principal.getUsername(),
                "message", "Admin role granted");
    }
}
