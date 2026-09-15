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
import com.cinema.model.User;
import com.cinema.repository.ClientRepository;
import com.cinema.repository.LicenseRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.security.SecurityUtils;
import com.cinema.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    @Mock
    private LicenseRepository licenseRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SecurityUtils securityUtils;

    private final LicenseMapper licenseMapper = new LicenseMapper();

    private LicenseService licenseService;

    private User distributor;
    private Movie movie;
    private Client client;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        licenseService = new LicenseService(
                licenseRepository,
                movieRepository,
                clientRepository,
                licenseMapper,
                securityUtils);

        distributor = TestDataFactory.distributor();
        movie = TestDataFactory.availableMovie();
        client = TestDataFactory.clientProfile();
        startDate = LocalDate.of(2026, 1, 1);
        endDate = LocalDate.of(2026, 12, 31);
    }

    @Test
    void create_whenEndBeforeStart_throwsBadRequest() {
        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, startDate.minusDays(1), "terms", 100L, 10L);

        assertThatThrownBy(() -> licenseService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("End date must be on or after start date");

        verify(licenseRepository, never()).save(any());
    }

    @Test
    void create_whenEndEqualsStart_succeeds() {
        when(securityUtils.getCurrentUser()).thenReturn(distributor);
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
        when(licenseRepository.save(any(License.class))).thenAnswer(invocation -> {
            License license = invocation.getArgument(0);
            license.setId(200L);
            return license;
        });

        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, startDate, "Same-day terms", 100L, 10L);

        LicenseResponse response = licenseService.create(request);

        assertThat(response.status()).isEqualTo(LicenseStatus.ACTIVE);
        assertThat(response.startDate()).isEqualTo(startDate);
        assertThat(response.endDate()).isEqualTo(startDate);
        verify(licenseRepository).save(any(License.class));
    }

    @Test
    void create_whenClientRole_throwsForbidden() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.clientUser());
        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, endDate, "terms", 100L, 10L);

        assertThatThrownBy(() -> licenseService.create(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only distributors can create licenses");

        verify(licenseRepository, never()).save(any());
    }

    @Test
    void create_whenMovieMissing_throwsNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(distributor);
        when(movieRepository.findById(100L)).thenReturn(Optional.empty());
        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, endDate, "terms", 100L, 10L);

        assertThatThrownBy(() -> licenseService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found with id: 100");
    }

    @Test
    void create_whenClientMissing_throwsNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(distributor);
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(clientRepository.findById(10L)).thenReturn(Optional.empty());
        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, endDate, "terms", 100L, 10L);

        assertThatThrownBy(() -> licenseService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with id: 10");
    }

    @Test
    void create_whenDistributorLicensesOthersMovie_throwsForbidden() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.otherDistributor());
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, endDate, "terms", 100L, 10L);

        assertThatThrownBy(() -> licenseService.create(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You can only license movies you distribute");

        verify(licenseRepository, never()).save(any());
    }

    @Test
    void create_whenAdmin_canLicenseAnyMovie() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
        when(licenseRepository.save(any(License.class))).thenAnswer(invocation -> {
            License license = invocation.getArgument(0);
            license.setId(201L);
            return license;
        });

        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, endDate, "Admin license", 100L, 10L);

        LicenseResponse response = licenseService.create(request);

        assertThat(response.status()).isEqualTo(LicenseStatus.ACTIVE);
        assertThat(response.distributorId()).isEqualTo(1L);
        verify(licenseRepository).save(any(License.class));
    }

    @Test
    void create_success_persistsActiveLicense() {
        when(securityUtils.getCurrentUser()).thenReturn(distributor);
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));

        ArgumentCaptor<License> licenseCaptor = ArgumentCaptor.forClass(License.class);
        when(licenseRepository.save(licenseCaptor.capture())).thenAnswer(invocation -> {
            License license = invocation.getArgument(0);
            license.setId(200L);
            return license;
        });

        CreateLicenseRequest request = new CreateLicenseRequest(
                startDate, endDate, "Standard distribution terms", 100L, 10L);

        LicenseResponse response = licenseService.create(request);

        assertThat(response.id()).isEqualTo(200L);
        assertThat(response.status()).isEqualTo(LicenseStatus.ACTIVE);
        assertThat(response.movieId()).isEqualTo(100L);
        assertThat(response.clientId()).isEqualTo(10L);
        assertThat(licenseCaptor.getValue().getStatus()).isEqualTo(LicenseStatus.ACTIVE);
        // No duplicate-license check exists in LicenseService.create — do not assert existsBy...
        verify(licenseRepository, never()).existsByMovieIdAndClientIdAndStatus(any(), any(), any());
    }

    @Test
    void updateStatus_setsExpired() {
        License license = TestDataFactory.activeLicense();
        when(licenseRepository.findById(200L)).thenReturn(Optional.of(license));

        LicenseResponse response = licenseService.updateStatus(
                200L, new UpdateLicenseStatusRequest(LicenseStatus.EXPIRED));

        assertThat(response.status()).isEqualTo(LicenseStatus.EXPIRED);
        assertThat(license.getStatus()).isEqualTo(LicenseStatus.EXPIRED);
    }

    @Test
    void updateStatus_setsActive() {
        License license = TestDataFactory.activeLicense();
        license.setStatus(LicenseStatus.EXPIRED);
        when(licenseRepository.findById(200L)).thenReturn(Optional.of(license));

        LicenseResponse response = licenseService.updateStatus(
                200L, new UpdateLicenseStatusRequest(LicenseStatus.ACTIVE));

        assertThat(response.status()).isEqualTo(LicenseStatus.ACTIVE);
    }

    @Test
    void findById_whenOtherDistributor_throwsForbidden() {
        License license = TestDataFactory.activeLicense();
        when(licenseRepository.findById(200L)).thenReturn(Optional.of(license));
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.otherDistributor());

        assertThatThrownBy(() -> licenseService.findById(200L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You do not have permission to view this license");
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(licenseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> licenseService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("License not found with id: 999");
    }
}
