package com.cinema.service;

import com.cinema.dto.request.CreateMovieRequest;
import com.cinema.dto.request.UpdateMovieRequest;
import com.cinema.dto.request.UpdateMovieStatusRequest;
import com.cinema.dto.response.MovieResponse;
import com.cinema.exception.ForbiddenException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.mapper.MovieMapper;
import com.cinema.model.Movie;
import com.cinema.model.MovieAvailabilityStatus;
import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.specification.EntitySpecifications;
import com.cinema.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public Page<MovieResponse> findAll(
            String title, String genre, MovieAvailabilityStatus availabilityStatus, Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Long distributorId = currentUser.getRole() == Role.DISTRIBUTOR ? currentUser.getId() : null;

        return movieRepository
                .findAll(EntitySpecifications.movieFilter(title, genre, availabilityStatus, distributorId), pageable)
                .map(movieMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MovieResponse findById(Long id) {
        return movieMapper.toResponse(getMovieOrThrow(id));
    }

    @Transactional
    public MovieResponse create(CreateMovieRequest request) {
        User distributor = securityUtils.getCurrentUser();
        if (distributor.getRole() != Role.DISTRIBUTOR && distributor.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only distributors can create movies");
        }

        Movie movie = movieMapper.toEntity(request, distributor);
        return movieMapper.toResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse update(Long id, UpdateMovieRequest request) {
        Movie movie = getMovieOrThrow(id);
        verifyMovieOwnership(movie);
        movieMapper.updateEntity(movie, request);
        return movieMapper.toResponse(movie);
    }

    @Transactional
    public MovieResponse updateStatus(Long id, UpdateMovieStatusRequest request) {
        Movie movie = getMovieOrThrow(id);
        verifyMovieOwnership(movie);
        movie.setAvailabilityStatus(request.availabilityStatus());
        return movieMapper.toResponse(movie);
    }

    @Transactional
    public void delete(Long id) {
        Movie movie = getMovieOrThrow(id);
        movieRepository.delete(movie);
    }

    private Movie getMovieOrThrow(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    private void verifyMovieOwnership(Movie movie) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.DISTRIBUTOR
                && movie.getDistributor().getId().equals(currentUser.getId())) {
            return;
        }
        throw new ForbiddenException("You do not have permission to modify this movie");
    }
}
