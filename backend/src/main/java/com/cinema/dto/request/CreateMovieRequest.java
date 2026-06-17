package com.cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateMovieRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 100) String genre,
        @NotNull LocalDate releaseDate,
        @NotNull @Positive Integer duration,
        String description
) {
}
