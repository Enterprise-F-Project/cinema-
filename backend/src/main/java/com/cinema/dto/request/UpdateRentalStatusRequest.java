package com.cinema.dto.request;

import com.cinema.model.RentalStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRentalStatusRequest(
        @NotNull(message = "Rental status is required")
        RentalStatus status
) {
}
