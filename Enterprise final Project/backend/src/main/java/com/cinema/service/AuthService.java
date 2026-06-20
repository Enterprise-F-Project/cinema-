package com.cinema.service;

import com.cinema.dto.request.LoginRequest;
import com.cinema.dto.request.RegisterRequest;
import com.cinema.dto.response.MessageResponse;
import com.cinema.dto.response.TokenResponse;
import com.cinema.dto.response.UserResponse;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ConflictException;
import com.cinema.mapper.UserMapper;
import com.cinema.model.Client;
import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.ClientRepository;
import com.cinema.repository.UserRepository;
import com.cinema.security.JwtService;
import com.cinema.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .active(true)
                .build();
        userRepository.save(user);

        if (request.role() == Role.CLIENT) {
            if (!clientRepository.existsByEmail(request.email())) {
                Client client = Client.builder()
                        .organizationName(request.fullName())
                        .email(request.email())
                        .build();
                clientRepository.save(client);
            }
        }

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!user.getActive()) {
            throw new BadRequestException("Account is disabled");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new TokenResponse(token, "Bearer", jwtService.getExpirationMs(), user.getRole());
    }

    public MessageResponse logout(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            tokenBlacklistService.blacklist(authorization.substring(7));
        }
        return new MessageResponse("Logged out successfully");
    }
}
