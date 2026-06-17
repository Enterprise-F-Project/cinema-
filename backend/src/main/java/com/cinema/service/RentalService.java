package com.cinema.service;

import com.cinema.dto.request.CreateRentalRequest;
import com.cinema.dto.request.UpdateRentalStatusRequest;
import com.cinema.dto.response.RentalResponse;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ForbiddenException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.mapper.RentalMapper;
import com.cinema.model.Client;
import com.cinema.model.LicenseStatus;
import com.cinema.model.Movie;
import com.cinema.model.MovieAvailabilityStatus;
import com.cinema.model.Rental;
import com.cinema.model.RentalStatus;
import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.ClientRepository;
import com.cinema.repository.LicenseRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.RentalRepository;
import com.cinema.repository.specification.EntitySpecifications;
import com.cinema.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MovieRepository movieRepository;
    private final ClientRepository clientRepository;
    private final LicenseRepository licenseRepository;
    private final RentalMapper rentalMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public Page<RentalResponse> findAll(RentalStatus status, Long movieId, Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Long clientId = null;

        if (currentUser.getRole() == Role.CLIENT) {
            clientId = getClientForCurrentUser().getId();
        }

        return rentalRepository
                .findAll(EntitySpecifications.rentalFilter(status, movieId, clientId), pageable)
                .map(rentalMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public RentalResponse findById(Long id) {
        Rental rental = getRentalOrThrow(id);
        verifyRentalAccess(rental);
        return rentalMapper.toResponse(rental);
    }

    @Transactional
    public RentalResponse create(CreateRentalRequest request) {
        if (request.returnDate().isBefore(request.rentalDate())) {
            throw new BadRequestException("Return date must be on or after rental date");
        }

        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() != Role.CLIENT) {
            throw new ForbiddenException("Only clients can create rentals");
        }

        Client client = getClientForCurrentUser();
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.movieId()));

        if (!licenseRepository.existsByMovieIdAndClientIdAndStatus(
                request.movieId(), client.getId(), LicenseStatus.ACTIVE)) {
            throw new BadRequestException("An active license is required to rent this movie");
        }

        if (movie.getAvailabilityStatus() != MovieAvailabilityStatus.AVAILABLE) {
            throw new BadRequestException("Movie is not available for rental");
        }

        Rental rental = rentalMapper.toEntity(request, movie, client);
        return rentalMapper.toResponse(rentalRepository.save(rental));
    }

    @Transactional
    public RentalResponse updateStatus(Long id, UpdateRentalStatusRequest request) {
        Rental rental = getRentalOrThrow(id);
        verifyRentalAccess(rental);

        RentalStatus newStatus = request.status();
        validateStatusTransition(rental.getStatus(), newStatus);

        rental.setStatus(newStatus);
        updateMovieAvailability(rental.getMovie(), newStatus);

        return rentalMapper.toResponse(rental);
    }

    private void validateStatusTransition(RentalStatus current, RentalStatus next) {
        boolean valid = switch (current) {
            case REQUESTED -> next == RentalStatus.ACTIVE || next == RentalStatus.COMPLETED;
            case ACTIVE -> next == RentalStatus.COMPLETED;
            case COMPLETED -> false;
        };
        if (!valid) {
            throw new BadRequestException("Invalid rental status transition from " + current + " to " + next);
        }
    }

    private void updateMovieAvailability(Movie movie, RentalStatus rentalStatus) {
        if (rentalStatus == RentalStatus.ACTIVE) {
            movie.setAvailabilityStatus(MovieAvailabilityStatus.RENTED);
        } else if (rentalStatus == RentalStatus.COMPLETED) {
            movie.setAvailabilityStatus(MovieAvailabilityStatus.AVAILABLE);
        }
    }

    private Client getClientForCurrentUser() {
        User currentUser = securityUtils.getCurrentUser();
        return clientRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found for current user"));
    }

    private Rental getRentalOrThrow(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));
    }

    private void verifyRentalAccess(Rental rental) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.CLIENT) {
            Client client = getClientForCurrentUser();
            if (rental.getClient().getId().equals(client.getId())) {
                return;
            }
        }
        throw new ForbiddenException("You do not have permission to access this rental");
    }
}
