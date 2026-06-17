package com.cinema.dto.response;

import com.cinema.model.MovieAvailabilityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovieResponse(
        Long id,
        String title,
        String genre,
        LocalDate releaseDate,
        Integer duration,
        String description,
        MovieAvailabilityStatus availabilityStatus,
        Long distributorId,
        String distributorName,
        LocalDateTime createdAt
) {
}
