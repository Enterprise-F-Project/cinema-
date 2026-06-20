package com.cinema.service;

import com.cinema.dto.request.UpdateUserStatusRequest;
import com.cinema.dto.response.DashboardResponse;
import com.cinema.dto.response.UserResponse;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.mapper.UserMapper;
import com.cinema.model.LicenseStatus;
import com.cinema.model.RentalStatus;
import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.ClientRepository;
import com.cinema.repository.LicenseRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.RentalRepository;
import com.cinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ClientRepository clientRepository;
    private final LicenseRepository licenseRepository;
    private final RentalRepository rentalRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Role role, Boolean active, Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse)
                .map(user -> user);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(request.active());
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                userRepository.count(),
                movieRepository.count(),
                clientRepository.count(),
                licenseRepository.countByStatus(LicenseStatus.ACTIVE),
                rentalRepository.countByStatus(RentalStatus.ACTIVE)
        );
    }
}
