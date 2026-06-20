package com.cinema.controller;

import com.cinema.dto.request.CreateLicenseRequest;
import com.cinema.dto.request.UpdateLicenseStatusRequest;
import com.cinema.dto.response.LicenseResponse;
import com.cinema.model.LicenseStatus;
import com.cinema.service.LicenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
@Tag(name = "Licenses")
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'ADMIN')")
    @Operation(summary = "Get paginated licenses with optional filters")
    public ResponseEntity<Page<LicenseResponse>> findAll(
            @RequestParam(required = false) LicenseStatus status,
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateTo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(licenseService.findAll(
                status, movieId, clientId, startDateFrom, startDateTo, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'ADMIN')")
    @Operation(summary = "Get license by id")
    public ResponseEntity<LicenseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(licenseService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'ADMIN')")
    @Operation(summary = "Create a new license")
    public ResponseEntity<LicenseResponse> create(@Valid @RequestBody CreateLicenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.create(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update license status")
    public ResponseEntity<LicenseResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLicenseStatusRequest request) {
        return ResponseEntity.ok(licenseService.updateStatus(id, request));
    }
}
