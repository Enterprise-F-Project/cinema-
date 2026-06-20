package com.cinema.service;

import com.cinema.dto.request.CreateLicenseRequest;
import com.cinema.dto.request.UpdateLicenseStatusRequest;
import com.cinema.dto.response.LicenseResponse;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ForbiddenException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.mapper.LicenseMapper;
import com.cinema.model.Client;
import com.cinema.model.License;
import com.cinema.model.LicenseStatus;
import com.cinema.model.Movie;
import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.ClientRepository;
import com.cinema.repository.LicenseRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.specification.EntitySpecifications;
import com.cinema.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final MovieRepository movieRepository;
    private final ClientRepository clientRepository;
    private final LicenseMapper licenseMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public Page<LicenseResponse> findAll(
            LicenseStatus status, Long movieId, Long clientId,
            LocalDate startDateFrom, LocalDate startDateTo, Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Long distributorId = currentUser.getRole() == Role.DISTRIBUTOR ? currentUser.getId() : null;

        return licenseRepository
                .findAll(EntitySpecifications.licenseFilter(
                        status, movieId, clientId, distributorId, startDateFrom, startDateTo), pageable)
                .map(licenseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LicenseResponse findById(Long id) {
        License license = getLicenseOrThrow(id);
        verifyLicenseAccess(license);
        return licenseMapper.toResponse(license);
    }

    @Transactional
    public LicenseResponse create(CreateLicenseRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new BadRequestException("End date must be on or after start date");
        }

        User distributor = securityUtils.getCurrentUser();
        if (distributor.getRole() != Role.DISTRIBUTOR && distributor.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only distributors can create licenses");
        }

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.movieId()));
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.clientId()));

        if (distributor.getRole() == Role.DISTRIBUTOR
                && !movie.getDistributor().getId().equals(distributor.getId())) {
            throw new ForbiddenException("You can only license movies you distribute");
        }

        License license = licenseMapper.toEntity(request, movie, distributor, client);
        return licenseMapper.toResponse(licenseRepository.save(license));
    }

    @Transactional
    public LicenseResponse updateStatus(Long id, UpdateLicenseStatusRequest request) {
        License license = getLicenseOrThrow(id);
        license.setStatus(request.status());
        return licenseMapper.toResponse(license);
    }

    private License getLicenseOrThrow(Long id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("License not found with id: " + id));
    }

    private void verifyLicenseAccess(License license) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.DISTRIBUTOR
                && license.getDistributor().getId().equals(currentUser.getId())) {
            return;
        }
        throw new ForbiddenException("You do not have permission to view this license");
    }
}
