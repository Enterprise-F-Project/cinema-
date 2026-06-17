package com.cinema.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateRentalRequest(
        @NotNull Long movieId,
        @NotNull LocalDateTime rentalDate,
        @NotNull LocalDateTime returnDate
) {
}
