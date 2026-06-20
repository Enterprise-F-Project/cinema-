package com.cinema.mapper;

import com.cinema.dto.request.CreateMovieRequest;
import com.cinema.dto.request.UpdateMovieRequest;
import com.cinema.dto.response.MovieResponse;
import com.cinema.model.Movie;
import com.cinema.model.MovieAvailabilityStatus;
import com.cinema.model.User;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(CreateMovieRequest request, User distributor) {
        return Movie.builder()
                .title(request.title())
                .genre(request.genre())
                .releaseDate(request.releaseDate())
                .duration(request.duration())
                .description(request.description())
                .availabilityStatus(MovieAvailabilityStatus.AVAILABLE)
                .distributor(distributor)
                .build();
    }

    public void updateEntity(Movie movie, UpdateMovieRequest request) {
        movie.setTitle(request.title());
        movie.setGenre(request.genre());
        movie.setReleaseDate(request.releaseDate());
        movie.setDuration(request.duration());
        movie.setDescription(request.description());
    }

    public MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getReleaseDate(),
                movie.getDuration(),
                movie.getDescription(),
                movie.getAvailabilityStatus(),
                movie.getDistributor().getId(),
                movie.getDistributor().getFullName(),
                movie.getCreatedAt()
        );
    }
}
