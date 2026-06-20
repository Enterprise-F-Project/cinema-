package com.cinema.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateRentalRequest(
        @NotNull(message = "Movie ID is required")
        Long movieId,

        @NotNull(message = "Rental date is required")
        LocalDateTime rentalDate,

        @NotNull(message = "Return date is required")
        LocalDateTime returnDate
) {
}
