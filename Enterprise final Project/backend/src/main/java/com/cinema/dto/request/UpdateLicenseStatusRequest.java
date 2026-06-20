package com.cinema.dto.request;

import com.cinema.model.LicenseStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateLicenseStatusRequest(
        @NotNull(message = "License status is required")
        LicenseStatus status
) {
}
