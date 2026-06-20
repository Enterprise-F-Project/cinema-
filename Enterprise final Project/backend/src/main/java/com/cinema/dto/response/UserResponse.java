package com.cinema.dto.response;

import com.cinema.model.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        Boolean active,
        LocalDateTime createdAt
) {
}
