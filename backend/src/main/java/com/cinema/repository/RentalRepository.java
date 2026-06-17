package com.cinema.repository;

import com.cinema.model.Rental;
import com.cinema.model.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {

    long countByStatus(RentalStatus status);
}
