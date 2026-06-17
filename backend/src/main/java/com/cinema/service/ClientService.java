package com.cinema.service;

import com.cinema.dto.request.CreateClientRequest;
import com.cinema.dto.request.UpdateClientRequest;
import com.cinema.dto.response.ClientResponse;
import com.cinema.exception.ConflictException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.mapper.ClientMapper;
import com.cinema.model.Client;
import com.cinema.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    public Page<ClientResponse> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable).map(clientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        return clientMapper.toResponse(getClientOrThrow(id));
    }

    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new ConflictException("Client email already exists");
        }
        Client client = clientMapper.toEntity(request);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request) {
        Client client = getClientOrThrow(id);
        if (!client.getEmail().equals(request.email()) && clientRepository.existsByEmail(request.email())) {
            throw new ConflictException("Client email already exists");
        }
        clientMapper.updateEntity(client, request);
        return clientMapper.toResponse(client);
    }

    private Client getClientOrThrow(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }
}
