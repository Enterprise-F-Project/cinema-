package com.cinema.controller;

import com.cinema.dto.request.CreateRentalRequest;
import com.cinema.dto.request.UpdateRentalStatusRequest;
import com.cinema.dto.response.RentalResponse;
import com.cinema.model.RentalStatus;
import com.cinema.service.RentalService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@Tag(name = "Rentals")
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    @Operation(summary = "Get paginated rentals with optional filters")
    public ResponseEntity<Page<RentalResponse>> findAll(
            @RequestParam(required = false) RentalStatus status,
            @RequestParam(required = false) Long movieId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(rentalService.findAll(status, movieId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    @Operation(summary = "Get rental by id")
    public ResponseEntity<RentalResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Create a new rental request")
    public ResponseEntity<RentalResponse> create(@Valid @RequestBody CreateRentalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.create(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    @Operation(summary = "Update rental status")
    public ResponseEntity<RentalResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRentalStatusRequest request) {
        return ResponseEntity.ok(rentalService.updateStatus(id, request));
    }
}
