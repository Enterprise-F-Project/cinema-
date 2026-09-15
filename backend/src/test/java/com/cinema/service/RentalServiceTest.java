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
import com.cinema.model.User;
import com.cinema.repository.ClientRepository;
import com.cinema.repository.LicenseRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.RentalRepository;
import com.cinema.security.SecurityUtils;
import com.cinema.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private LicenseRepository licenseRepository;
    @Mock
    private SecurityUtils securityUtils;

    private final RentalMapper rentalMapper = new RentalMapper();

    private RentalService rentalService;

    private User clientUser;
    private Client clientProfile;
    private Movie availableMovie;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;

    @BeforeEach
    void setUp() {
        rentalService = new RentalService(
                rentalRepository,
                movieRepository,
                clientRepository,
                licenseRepository,
                rentalMapper,
                securityUtils);

        clientUser = TestDataFactory.clientUser();
        clientProfile = TestDataFactory.clientProfile();
        availableMovie = TestDataFactory.availableMovie();
        rentalDate = LocalDateTime.of(2026, 3, 1, 10, 0);
        returnDate = LocalDateTime.of(2026, 3, 8, 10, 0);
    }

    // --- create: BVA + decision table ---

    @Test
    void create_whenReturnBeforeRental_throwsBadRequest() {
        CreateRentalRequest request = new CreateRentalRequest(
                100L, rentalDate, rentalDate.minusDays(1));

        assertThatThrownBy(() -> rentalService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Return date must be on or after rental date");

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void create_whenReturnEqualsRental_succeeds() {
        stubSuccessfulCreatePreconditions();
        CreateRentalRequest request = new CreateRentalRequest(100L, rentalDate, rentalDate);

        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental saved = invocation.getArgument(0);
            saved.setId(300L);
            return saved;
        });

        RentalResponse response = rentalService.create(request);

        assertThat(response.status()).isEqualTo(RentalStatus.REQUESTED);
        assertThat(response.rentalDate()).isEqualTo(rentalDate);
        assertThat(response.returnDate()).isEqualTo(rentalDate);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void create_whenCallerNotClient_throwsForbidden() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());
        CreateRentalRequest request = new CreateRentalRequest(100L, rentalDate, returnDate);

        assertThatThrownBy(() -> rentalService.create(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only clients can create rentals");

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void create_whenClientProfileMissing_throwsNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.empty());
        CreateRentalRequest request = new CreateRentalRequest(100L, rentalDate, returnDate);

        assertThatThrownBy(() -> rentalService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client profile not found for current user");
    }

    @Test
    void create_whenMovieMissing_throwsNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));
        when(movieRepository.findById(100L)).thenReturn(Optional.empty());
        CreateRentalRequest request = new CreateRentalRequest(100L, rentalDate, returnDate);

        assertThatThrownBy(() -> rentalService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found with id: 100");
    }

    @Test
    void create_whenNoActiveLicense_throwsBadRequest() {
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));
        when(movieRepository.findById(100L)).thenReturn(Optional.of(availableMovie));
        when(licenseRepository.existsByMovieIdAndClientIdAndStatus(
                100L, 10L, LicenseStatus.ACTIVE)).thenReturn(false);
        CreateRentalRequest request = new CreateRentalRequest(100L, rentalDate, returnDate);

        assertThatThrownBy(() -> rentalService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("An active license is required to rent this movie");

        verify(licenseRepository).existsByMovieIdAndClientIdAndStatus(
                100L, 10L, LicenseStatus.ACTIVE);
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void create_whenMovieRented_throwsBadRequest() {
        Movie rented = TestDataFactory.rentedMovie();
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));
        when(movieRepository.findById(100L)).thenReturn(Optional.of(rented));
        when(licenseRepository.existsByMovieIdAndClientIdAndStatus(
                100L, 10L, LicenseStatus.ACTIVE)).thenReturn(true);
        CreateRentalRequest request = new CreateRentalRequest(100L, rentalDate, returnDate);

        assertThatThrownBy(() -> rentalService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Movie is not available for rental");

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void create_whenAllPreconditionsMet_savesRequestedRental() {
        stubSuccessfulCreatePreconditions();
        CreateRentalRequest request = new CreateRentalRequest(100L, rentalDate, returnDate);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        when(rentalRepository.save(rentalCaptor.capture())).thenAnswer(invocation -> {
            Rental saved = invocation.getArgument(0);
            saved.setId(300L);
            return saved;
        });

        RentalResponse response = rentalService.create(request);

        assertThat(response.id()).isEqualTo(300L);
        assertThat(response.status()).isEqualTo(RentalStatus.REQUESTED);
        assertThat(response.movieId()).isEqualTo(100L);
        assertThat(response.clientId()).isEqualTo(10L);
        assertThat(rentalCaptor.getValue().getStatus()).isEqualTo(RentalStatus.REQUESTED);
        assertThat(availableMovie.getAvailabilityStatus()).isEqualTo(MovieAvailabilityStatus.AVAILABLE);
        verify(licenseRepository).existsByMovieIdAndClientIdAndStatus(
                eq(100L), eq(10L), eq(LicenseStatus.ACTIVE));
    }

    // --- updateStatus: state transition + side effects + access ---

    @Test
    void updateStatus_requestedToActive_setsMovieRented() {
        Rental rental = TestDataFactory.requestedRental();
        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));

        RentalResponse response = rentalService.updateStatus(
                300L, new UpdateRentalStatusRequest(RentalStatus.ACTIVE));

        assertThat(response.status()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(rental.getStatus()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(rental.getMovie().getAvailabilityStatus()).isEqualTo(MovieAvailabilityStatus.RENTED);
    }

    @Test
    void updateStatus_requestedToCompleted_setsMovieAvailable() {
        Rental rental = TestDataFactory.requestedRental();
        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));

        RentalResponse response = rentalService.updateStatus(
                300L, new UpdateRentalStatusRequest(RentalStatus.COMPLETED));

        assertThat(response.status()).isEqualTo(RentalStatus.COMPLETED);
        assertThat(rental.getMovie().getAvailabilityStatus()).isEqualTo(MovieAvailabilityStatus.AVAILABLE);
    }

    @Test
    void updateStatus_activeToCompleted_setsMovieAvailable() {
        Rental rental = TestDataFactory.activeRental();
        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));

        RentalResponse response = rentalService.updateStatus(
                300L, new UpdateRentalStatusRequest(RentalStatus.COMPLETED));

        assertThat(response.status()).isEqualTo(RentalStatus.COMPLETED);
        assertThat(rental.getMovie().getAvailabilityStatus()).isEqualTo(MovieAvailabilityStatus.AVAILABLE);
    }

    @Test
    void updateStatus_completedToActive_throwsBadRequest() {
        Rental rental = TestDataFactory.completedRental();
        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));

        assertThatThrownBy(() -> rentalService.updateStatus(
                300L, new UpdateRentalStatusRequest(RentalStatus.ACTIVE)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid rental status transition from COMPLETED to ACTIVE");
    }

    @Test
    void updateStatus_activeToRequested_throwsBadRequest() {
        Rental rental = TestDataFactory.activeRental();
        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));

        assertThatThrownBy(() -> rentalService.updateStatus(
                300L, new UpdateRentalStatusRequest(RentalStatus.REQUESTED)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid rental status transition from ACTIVE to REQUESTED");
    }

    @Test
    void updateStatus_whenOtherClient_throwsForbidden() {
        Rental rental = TestDataFactory.requestedRental();
        User otherUser = TestDataFactory.otherClientUser();
        Client otherClient = TestDataFactory.otherClientProfile();

        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(otherUser);
        when(clientRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherClient));

        assertThatThrownBy(() -> rentalService.updateStatus(
                300L, new UpdateRentalStatusRequest(RentalStatus.ACTIVE)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You do not have permission to access this rental");
    }

    @Test
    void updateStatus_whenAdmin_allowsTransition() {
        Rental rental = TestDataFactory.requestedRental();
        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());

        RentalResponse response = rentalService.updateStatus(
                300L, new UpdateRentalStatusRequest(RentalStatus.ACTIVE));

        assertThat(response.status()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(rental.getMovie().getAvailabilityStatus()).isEqualTo(MovieAvailabilityStatus.RENTED);
        verify(clientRepository, never()).findByEmail(any());
    }

    // --- findById ---

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Rental not found with id: 999");
    }

    @Test
    void findById_whenOtherClient_throwsForbidden() {
        Rental rental = TestDataFactory.requestedRental();
        User otherUser = TestDataFactory.otherClientUser();
        Client otherClient = TestDataFactory.otherClientProfile();

        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(otherUser);
        when(clientRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherClient));

        assertThatThrownBy(() -> rentalService.findById(300L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You do not have permission to access this rental");
    }

    @Test
    void findById_whenAdmin_allowsAccess() {
        Rental rental = TestDataFactory.requestedRental();
        when(rentalRepository.findById(300L)).thenReturn(Optional.of(rental));
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());

        RentalResponse response = rentalService.findById(300L);

        assertThat(response.id()).isEqualTo(300L);
        assertThat(response.status()).isEqualTo(RentalStatus.REQUESTED);
    }

    // --- findAll ---

    @Test
    @SuppressWarnings("unchecked")
    void findAll_whenClient_scopesToOwnClient() {
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));
        Pageable pageable = PageRequest.of(0, 10);
        when(rentalRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(TestDataFactory.requestedRental())));

        Page<RentalResponse> page = rentalService.findAll(null, null, pageable);

        assertThat(page.getContent()).hasSize(1);
        verify(clientRepository).findByEmail("client@cinema.com");
        verify(rentalRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_whenAdmin_doesNotForceClientFilter() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());
        Pageable pageable = PageRequest.of(0, 10);
        when(rentalRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        rentalService.findAll(null, null, pageable);

        verify(clientRepository, never()).findByEmail(any());
        verify(rentalRepository).findAll(any(Specification.class), eq(pageable));
    }

    private void stubSuccessfulCreatePreconditions() {
        when(securityUtils.getCurrentUser()).thenReturn(clientUser);
        when(clientRepository.findByEmail(clientUser.getEmail())).thenReturn(Optional.of(clientProfile));
        when(movieRepository.findById(100L)).thenReturn(Optional.of(availableMovie));
        when(licenseRepository.existsByMovieIdAndClientIdAndStatus(
                100L, 10L, LicenseStatus.ACTIVE)).thenReturn(true);
    }
}
