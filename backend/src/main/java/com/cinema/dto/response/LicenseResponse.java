package com.cinema.dto.response;

import com.cinema.model.LicenseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LicenseResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        String terms,
        LicenseStatus status,
        Long movieId,
        String movieTitle,
        Long distributorId,
        String distributorName,
        Long clientId,
        String clientName,
        LocalDateTime createdAt
) {
}
