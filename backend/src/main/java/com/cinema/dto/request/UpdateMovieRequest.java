package com.cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateMovieRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Genre is required")
        @Size(max = 100, message = "Genre must not exceed 100 characters")
        String genre,

        @NotNull(message = "Release date is required")
        LocalDate releaseDate,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be greater than 0")
        Integer duration,

        String description
) {
}
