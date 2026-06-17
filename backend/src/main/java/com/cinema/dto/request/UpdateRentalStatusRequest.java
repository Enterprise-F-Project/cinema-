package com.cinema.dto.request;

import com.cinema.model.RentalStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRentalStatusRequest(
        @NotNull RentalStatus status
) {
}
