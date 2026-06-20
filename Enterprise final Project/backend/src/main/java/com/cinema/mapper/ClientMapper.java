package com.cinema.mapper;

import com.cinema.dto.request.CreateClientRequest;
import com.cinema.dto.request.UpdateClientRequest;
import com.cinema.dto.response.ClientResponse;
import com.cinema.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(CreateClientRequest request) {
        return Client.builder()
                .organizationName(request.organizationName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .build();
    }

    public void updateEntity(Client client, UpdateClientRequest request) {
        client.setOrganizationName(request.organizationName());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());
    }

    public ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getOrganizationName(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress(),
                client.getCreatedAt()
        );
    }
}
