package com.cinema.mapper;

import com.cinema.dto.request.CreateLicenseRequest;
import com.cinema.dto.response.LicenseResponse;
import com.cinema.model.Client;
import com.cinema.model.License;
import com.cinema.model.LicenseStatus;
import com.cinema.model.Movie;
import com.cinema.model.User;
import org.springframework.stereotype.Component;

@Component
public class LicenseMapper {

    public License toEntity(CreateLicenseRequest request, Movie movie, User distributor, Client client) {
        return License.builder()
                .startDate(request.startDate())
                .endDate(request.endDate())
                .terms(request.terms())
                .status(LicenseStatus.ACTIVE)
                .movie(movie)
                .distributor(distributor)
                .client(client)
                .build();
    }

    public LicenseResponse toResponse(License license) {
        return new LicenseResponse(
                license.getId(),
                license.getStartDate(),
                license.getEndDate(),
                license.getTerms(),
                license.getStatus(),
                license.getMovie().getId(),
                license.getMovie().getTitle(),
                license.getDistributor().getId(),
                license.getDistributor().getFullName(),
                license.getClient().getId(),
                license.getClient().getOrganizationName(),
                license.getCreatedAt()
        );
    }
}
