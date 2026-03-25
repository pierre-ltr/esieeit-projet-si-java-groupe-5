package com.esieeit.projetsi.infrastructure.security;

import com.esieeit.projetsi.infrastructure.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String normalizedLogin = login.trim().toLowerCase(Locale.ROOT);
        return userRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(normalizedLogin, normalizedLogin)
                .map(AuthenticatedUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalizedLogin));
    }
}
