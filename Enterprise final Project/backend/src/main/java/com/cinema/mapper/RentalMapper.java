package com.cinema.mapper;

import com.cinema.dto.request.CreateRentalRequest;
import com.cinema.dto.response.RentalResponse;
import com.cinema.model.Client;
import com.cinema.model.Movie;
import com.cinema.model.Rental;
import com.cinema.model.RentalStatus;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    public Rental toEntity(CreateRentalRequest request, Movie movie, Client client) {
        return Rental.builder()
                .rentalDate(request.rentalDate())
                .returnDate(request.returnDate())
                .status(RentalStatus.REQUESTED)
                .movie(movie)
                .client(client)
                .build();
    }

    public RentalResponse toResponse(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                rental.getStatus(),
                rental.getMovie().getId(),
                rental.getMovie().getTitle(),
                rental.getClient().getId(),
                rental.getClient().getOrganizationName(),
                rental.getCreatedAt()
        );
    }
}
