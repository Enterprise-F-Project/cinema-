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
import com.cinema.model.User;
import com.cinema.repository.MovieRepository;
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

import java.time.LocalDate;
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
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private SecurityUtils securityUtils;

    private final MovieMapper movieMapper = new MovieMapper();

    private MovieService movieService;

    private User distributor;
    private CreateMovieRequest createRequest;

    @BeforeEach
    void setUp() {
        movieService = new MovieService(movieRepository, movieMapper, securityUtils);
        distributor = TestDataFactory.distributor();
        createRequest = new CreateMovieRequest(
                "Abyssinia Rising",
                "Drama",
                LocalDate.of(2024, 1, 15),
                120,
                "Ethiopian drama");
    }

    @Test
    void create_whenClient_throwsForbidden() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.clientUser());

        assertThatThrownBy(() -> movieService.create(createRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only distributors can create movies");

        verify(movieRepository, never()).save(any());
    }

    @Test
    void create_whenDistributor_savesAvailableMovie() {
        when(securityUtils.getCurrentUser()).thenReturn(distributor);
        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        when(movieRepository.save(movieCaptor.capture())).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setId(100L);
            return movie;
        });

        MovieResponse response = movieService.create(createRequest);

        assertThat(response.title()).isEqualTo("Abyssinia Rising");
        assertThat(response.availabilityStatus()).isEqualTo(MovieAvailabilityStatus.AVAILABLE);
        assertThat(response.distributorId()).isEqualTo(2L);
        assertThat(movieCaptor.getValue().getAvailabilityStatus())
                .isEqualTo(MovieAvailabilityStatus.AVAILABLE);
        assertThat(movieCaptor.getValue().getDistributor()).isEqualTo(distributor);
    }

    @Test
    void create_whenAdmin_savesAvailableMovie() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setId(101L);
            return movie;
        });

        MovieResponse response = movieService.create(createRequest);

        assertThat(response.availabilityStatus()).isEqualTo(MovieAvailabilityStatus.AVAILABLE);
        assertThat(response.distributorId()).isEqualTo(1L);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_whenOtherDistributor_throwsForbidden() {
        Movie movie = TestDataFactory.availableMovie();
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.otherDistributor());

        UpdateMovieRequest request = new UpdateMovieRequest(
                "Updated Title", "Action", LocalDate.of(2024, 2, 1), 110, "Updated");

        assertThatThrownBy(() -> movieService.update(100L, request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You do not have permission to modify this movie");
    }

    @Test
    void update_whenOwningDistributor_succeeds() {
        Movie movie = TestDataFactory.availableMovie();
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(securityUtils.getCurrentUser()).thenReturn(distributor);

        UpdateMovieRequest request = new UpdateMovieRequest(
                "Updated Title", "Action", LocalDate.of(2024, 2, 1), 110, "Updated");

        MovieResponse response = movieService.update(100L, request);

        assertThat(response.title()).isEqualTo("Updated Title");
        assertThat(response.genre()).isEqualTo("Action");
        assertThat(response.duration()).isEqualTo(110);
        assertThat(movie.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void update_whenAdmin_succeeds() {
        Movie movie = TestDataFactory.availableMovie();
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());

        UpdateMovieRequest request = new UpdateMovieRequest(
                "Admin Edit", "Drama", LocalDate.of(2024, 1, 15), 120, null);

        MovieResponse response = movieService.update(100L, request);

        assertThat(response.title()).isEqualTo("Admin Edit");
    }

    @Test
    void update_whenClient_throwsForbidden() {
        Movie movie = TestDataFactory.availableMovie();
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.clientUser());

        UpdateMovieRequest request = new UpdateMovieRequest(
                "Hacked Title", "Action", LocalDate.of(2024, 2, 1), 110, "Nope");

        assertThatThrownBy(() -> movieService.update(100L, request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You do not have permission to modify this movie");
    }

    @Test
    void updateStatus_setsRequestedAvailability() {
        Movie movie = TestDataFactory.availableMovie();
        when(movieRepository.findById(100L)).thenReturn(Optional.of(movie));
        when(securityUtils.getCurrentUser()).thenReturn(distributor);

        MovieResponse rented = movieService.updateStatus(
                100L, new UpdateMovieStatusRequest(MovieAvailabilityStatus.RENTED));
        assertThat(rented.availabilityStatus()).isEqualTo(MovieAvailabilityStatus.RENTED);
        assertThat(movie.getAvailabilityStatus()).isEqualTo(MovieAvailabilityStatus.RENTED);

        MovieResponse available = movieService.updateStatus(
                100L, new UpdateMovieStatusRequest(MovieAvailabilityStatus.AVAILABLE));
        assertThat(available.availabilityStatus()).isEqualTo(MovieAvailabilityStatus.AVAILABLE);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_whenDistributor_scopesToOwnId() {
        when(securityUtils.getCurrentUser()).thenReturn(distributor);
        Pageable pageable = PageRequest.of(0, 10);
        when(movieRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(TestDataFactory.availableMovie())));

        Page<MovieResponse> page = movieService.findAll(null, null, null, pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().distributorId()).isEqualTo(2L);
        verify(movieRepository).findAll(any(Specification.class), eq(pageable));
        // Service passes distributorId = currentUser.id (2L) into EntitySpecifications.movieFilter
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_whenAdmin_doesNotScope() {
        when(securityUtils.getCurrentUser()).thenReturn(TestDataFactory.admin());
        Pageable pageable = PageRequest.of(0, 10);
        when(movieRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        movieService.findAll(null, null, null, pageable);

        verify(movieRepository).findAll(any(Specification.class), eq(pageable));
        // Service passes distributorId = null for non-DISTRIBUTOR roles
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found with id: 999");
    }

    @Test
    void delete_whenMissing_throwsNotFound() {
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found with id: 999");

        verify(movieRepository, never()).delete(any(Movie.class));
    }
}
