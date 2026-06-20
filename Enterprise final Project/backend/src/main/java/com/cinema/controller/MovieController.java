package com.cinema.controller;

import com.cinema.dto.request.CreateMovieRequest;
import com.cinema.dto.request.UpdateMovieRequest;
import com.cinema.dto.request.UpdateMovieStatusRequest;
import com.cinema.dto.response.MovieResponse;
import com.cinema.model.MovieAvailabilityStatus;
import com.cinema.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated movies with optional filters")
    public ResponseEntity<Page<MovieResponse>> findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) MovieAvailabilityStatus availabilityStatus,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(movieService.findAll(title, genre, availabilityStatus, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get movie by id")
    public ResponseEntity<MovieResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'ADMIN')")
    @Operation(summary = "Create a new movie")
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody CreateMovieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'ADMIN')")
    @Operation(summary = "Update a movie")
    public ResponseEntity<MovieResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMovieRequest request) {
        return ResponseEntity.ok(movieService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'ADMIN')")
    @Operation(summary = "Update movie availability status")
    public ResponseEntity<MovieResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMovieStatusRequest request) {
        return ResponseEntity.ok(movieService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a movie")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
