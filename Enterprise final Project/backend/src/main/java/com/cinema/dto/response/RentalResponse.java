package com.cinema.dto.response;

import com.cinema.model.RentalStatus;

import java.time.LocalDateTime;

public record RentalResponse(
        Long id,
        LocalDateTime rentalDate,
        LocalDateTime returnDate,
        RentalStatus status,
        Long movieId,
        String movieTitle,
        Long clientId,
        String clientName,
        LocalDateTime createdAt
) {
}
