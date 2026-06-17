package com.cinema.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateLicenseRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        String terms,
        @NotNull Long movieId,
        @NotNull Long clientId
) {
}
