package com.cinema.repository.specification;

import com.cinema.model.License;
import com.cinema.model.LicenseStatus;
import com.cinema.model.Movie;
import com.cinema.model.MovieAvailabilityStatus;
import com.cinema.model.Rental;
import com.cinema.model.RentalStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class EntitySpecifications {

    private EntitySpecifications() {
    }

    public static Specification<Movie> movieFilter(
            String title, String genre, MovieAvailabilityStatus availabilityStatus, Long distributorId) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (title != null && !title.isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (genre != null && !genre.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("genre")), genre.toLowerCase()));
            }
            if (availabilityStatus != null) {
                predicates = cb.and(predicates, cb.equal(root.get("availabilityStatus"), availabilityStatus));
            }
            if (distributorId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("distributor").get("id"), distributorId));
            }
            return predicates;
        };
    }

    public static Specification<License> licenseFilter(
            LicenseStatus status, Long movieId, Long clientId, Long distributorId,
            LocalDate startDateFrom, LocalDate startDateTo) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            if (movieId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("movie").get("id"), movieId));
            }
            if (clientId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("client").get("id"), clientId));
            }
            if (distributorId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("distributor").get("id"), distributorId));
            }
            if (startDateFrom != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("startDate"), startDateFrom));
            }
            if (startDateTo != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("startDate"), startDateTo));
            }
            return predicates;
        };
    }

    public static Specification<Rental> rentalFilter(
            RentalStatus status, Long movieId, Long clientId) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            if (movieId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("movie").get("id"), movieId));
            }
            if (clientId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("client").get("id"), clientId));
            }
            return predicates;
        };
    }
}
