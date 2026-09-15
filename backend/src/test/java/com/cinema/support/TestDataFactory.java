package com.cinema.support;

import com.cinema.model.Client;
import com.cinema.model.License;
import com.cinema.model.LicenseStatus;
import com.cinema.model.Movie;
import com.cinema.model.MovieAvailabilityStatus;
import com.cinema.model.Rental;
import com.cinema.model.RentalStatus;
import com.cinema.model.Role;
import com.cinema.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Shared realistic fixtures for core service unit tests.
 * Does not touch production seed data.
 */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static User admin() {
        return User.builder()
                .id(1L)
                .fullName("Platform Admin")
                .email("admin@cinema.com")
                .password("encoded")
                .role(Role.ADMIN)
                .active(true)
                .build();
    }

    public static User distributor() {
        return User.builder()
                .id(2L)
                .fullName("Movie Distributor")
                .email("distributor@cinema.com")
                .password("encoded")
                .role(Role.DISTRIBUTOR)
                .active(true)
                .build();
    }

    public static User otherDistributor() {
        return User.builder()
                .id(5L)
                .fullName("Other Distributor")
                .email("other-distributor@cinema.com")
                .password("encoded")
                .role(Role.DISTRIBUTOR)
                .active(true)
                .build();
    }

    public static User clientUser() {
        return User.builder()
                .id(3L)
                .fullName("Cinema Client")
                .email("client@cinema.com")
                .password("encoded")
                .role(Role.CLIENT)
                .active(true)
                .build();
    }

    public static User otherClientUser() {
        return User.builder()
                .id(4L)
                .fullName("Other Client")
                .email("other@cinema.com")
                .password("encoded")
                .role(Role.CLIENT)
                .active(true)
                .build();
    }

    public static User inactiveClientUser() {
        return User.builder()
                .id(6L)
                .fullName("Disabled Client")
                .email("disabled@cinema.com")
                .password("encoded")
                .role(Role.CLIENT)
                .active(false)
                .build();
    }

    public static Client clientProfile() {
        return Client.builder()
                .id(10L)
                .organizationName("Demo Cinema")
                .email("client@cinema.com")
                .phone("+251900000000")
                .address("Addis Ababa, Ethiopia")
                .build();
    }

    public static Client otherClientProfile() {
        return Client.builder()
                .id(11L)
                .organizationName("Other Cinema")
                .email("other@cinema.com")
                .build();
    }

    public static Movie availableMovie() {
        return Movie.builder()
                .id(100L)
                .title("Abyssinia Rising")
                .genre("Drama")
                .releaseDate(LocalDate.of(2024, 1, 15))
                .duration(120)
                .description("Ethiopian drama")
                .availabilityStatus(MovieAvailabilityStatus.AVAILABLE)
                .distributor(distributor())
                .build();
    }

    public static Movie rentedMovie() {
        Movie movie = availableMovie();
        movie.setAvailabilityStatus(MovieAvailabilityStatus.RENTED);
        return movie;
    }

    public static License activeLicense() {
        return License.builder()
                .id(200L)
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .terms("Standard distribution terms")
                .status(LicenseStatus.ACTIVE)
                .movie(availableMovie())
                .distributor(distributor())
                .client(clientProfile())
                .build();
    }

    public static Rental requestedRental() {
        return Rental.builder()
                .id(300L)
                .rentalDate(LocalDateTime.of(2026, 3, 1, 10, 0))
                .returnDate(LocalDateTime.of(2026, 3, 8, 10, 0))
                .status(RentalStatus.REQUESTED)
                .movie(availableMovie())
                .client(clientProfile())
                .build();
    }

    public static Rental activeRental() {
        Rental rental = requestedRental();
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setMovie(rentedMovie());
        return rental;
    }

    public static Rental completedRental() {
        Rental rental = requestedRental();
        rental.setStatus(RentalStatus.COMPLETED);
        return rental;
    }
}
