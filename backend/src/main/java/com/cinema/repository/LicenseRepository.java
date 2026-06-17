package com.cinema.repository;

import com.cinema.model.License;
import com.cinema.model.LicenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LicenseRepository extends JpaRepository<License, Long>, JpaSpecificationExecutor<License> {

    boolean existsByMovieIdAndClientIdAndStatus(Long movieId, Long clientId, LicenseStatus status);

    long countByStatus(LicenseStatus status);
}
