package com.cinema.dto.response;

import com.cinema.model.Role;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Role role
) {
}
