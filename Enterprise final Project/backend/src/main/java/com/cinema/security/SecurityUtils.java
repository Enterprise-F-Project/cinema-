package com.cinema.security;

import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import com.cinema.exception.ForbiddenException;
import com.cinema.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ForbiddenException("Authentication required");
        }
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    public boolean isDistributor() {
        return getCurrentUser().getRole() == Role.DISTRIBUTOR;
    }

    public boolean isClient() {
        return getCurrentUser().getRole() == Role.CLIENT;
    }
}
