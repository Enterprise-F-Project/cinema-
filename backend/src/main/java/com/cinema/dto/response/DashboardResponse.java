package com.cinema.dto.response;

public record DashboardResponse(
        long totalUsers,
        long totalMovies,
        long totalClients,
        long activeLicenses,
        long activeRentals
) {
}
