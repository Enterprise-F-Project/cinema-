package com.cinema.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateLicenseRequest(
        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        String terms,

        @NotNull(message = "Movie ID is required")
        Long movieId,

        @NotNull(message = "Client ID is required")
        Long clientId
) {
}
