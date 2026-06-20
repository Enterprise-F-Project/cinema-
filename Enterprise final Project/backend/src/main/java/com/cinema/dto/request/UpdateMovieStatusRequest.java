package com.cinema.dto.request;

import com.cinema.model.MovieAvailabilityStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateMovieStatusRequest(
        @NotNull(message = "Availability status is required")
        MovieAvailabilityStatus availabilityStatus
) {
}
