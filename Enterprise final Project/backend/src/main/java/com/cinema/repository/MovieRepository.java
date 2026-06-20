package com.cinema.repository;

import com.cinema.model.Movie;
import com.cinema.model.MovieAvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    long countByAvailabilityStatus(MovieAvailabilityStatus status);
}
