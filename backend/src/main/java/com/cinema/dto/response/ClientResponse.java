package com.cinema.dto.response;

import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        String organizationName,
        String email,
        String phone,
        String address,
        LocalDateTime createdAt
) {
}
