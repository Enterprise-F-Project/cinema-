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
import com.cinema.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private AuthenticationManager authenticationManager;

    private final UserMapper userMapper = new UserMapper();

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                clientRepository,
                passwordEncoder,
                jwtService,
                tokenBlacklistService,
                authenticationManager,
                userMapper);
    }

    // --- register ---

    @Test
    void register_whenEmailExists_throwsConflict() {
        RegisterRequest request = new RegisterRequest(
                "Cinema Client", "client@cinema.com", "Password123!", Role.CLIENT);
        when(userRepository.existsByEmail("client@cinema.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void register_client_createsClientWhenMissing() {
        RegisterRequest request = new RegisterRequest(
                "Cinema Client", "client@cinema.com", "Password123!", Role.CLIENT);
        when(userRepository.existsByEmail("client@cinema.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$12$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3L);
            return user;
        });
        when(clientRepository.existsByEmail("client@cinema.com")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authService.register(request);

        assertThat(response.email()).isEqualTo("client@cinema.com");
        assertThat(response.role()).isEqualTo(Role.CLIENT);
        assertThat(response.active()).isTrue();

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(clientCaptor.capture());
        assertThat(clientCaptor.getValue().getOrganizationName()).isEqualTo("Cinema Client");
        assertThat(clientCaptor.getValue().getEmail()).isEqualTo("client@cinema.com");
    }

    @Test
    void register_client_skipsClientWhenEmailExists() {
        RegisterRequest request = new RegisterRequest(
                "Cinema Client", "client@cinema.com", "Password123!", Role.CLIENT);
        when(userRepository.existsByEmail("client@cinema.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$12$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3L);
            return user;
        });
        when(clientRepository.existsByEmail("client@cinema.com")).thenReturn(true);

        authService.register(request);

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void register_distributor_doesNotCreateClient() {
        RegisterRequest request = new RegisterRequest(
                "Movie Distributor", "distributor@cinema.com", "Password123!", Role.DISTRIBUTOR);
        when(userRepository.existsByEmail("distributor@cinema.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$12$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        UserResponse response = authService.register(request);

        assertThat(response.role()).isEqualTo(Role.DISTRIBUTOR);
        verify(clientRepository, never()).existsByEmail(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void register_admin_isAllowedAndDoesNotCreateClient() {
        // Documents current production behavior: self-registration as ADMIN is allowed.
        RegisterRequest request = new RegisterRequest(
                "Platform Admin", "admin@cinema.com", "Password123!", Role.ADMIN);
        when(userRepository.existsByEmail("admin@cinema.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$12$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = authService.register(request);

        assertThat(response.role()).isEqualTo(Role.ADMIN);
        assertThat(response.active()).isTrue();
        verify(userRepository).save(any(User.class));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void register_encodesPasswordBeforeSave() {
        RegisterRequest request = new RegisterRequest(
                "Cinema Client", "client@cinema.com", "Password123!", Role.CLIENT);
        when(userRepository.existsByEmail("client@cinema.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$12$encoded-hash");
        when(clientRepository.existsByEmail("client@cinema.com")).thenReturn(true);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3L);
            return user;
        });

        authService.register(request);

        verify(passwordEncoder).encode("Password123!");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("$2a$12$encoded-hash");
        assertThat(userCaptor.getValue().getPassword()).isNotEqualTo("Password123!");
    }

    // --- login ---

    @Test
    void login_whenInactive_throwsBadRequest() {
        User inactive = TestDataFactory.inactiveClientUser();
        LoginRequest request = new LoginRequest("disabled@cinema.com", "Password123!");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("disabled@cinema.com")).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Account is disabled");

        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void login_whenActive_returnsBearerToken() {
        User client = TestDataFactory.clientUser();
        LoginRequest request = new LoginRequest("client@cinema.com", "Password123!");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("client@cinema.com")).thenReturn(Optional.of(client));
        when(jwtService.generateToken("client@cinema.com", Role.CLIENT)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(86_400_000L);

        TokenResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(86_400_000L);
        assertThat(response.role()).isEqualTo(Role.CLIENT);
        verify(jwtService).generateToken(eq("client@cinema.com"), eq(Role.CLIENT));
    }

    @Test
    void login_whenUserMissing_throwsInvalidCredentials() {
        // authenticate succeeds, but user row is missing — AuthService throws BadRequestException.
        LoginRequest request = new LoginRequest("ghost@cinema.com", "Password123!");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("ghost@cinema.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid credentials");
    }

    // --- logout ---

    @Test
    void logout_withBearer_blacklistsToken() {
        MessageResponse response = authService.logout("Bearer abc");

        assertThat(response.message()).isEqualTo("Logged out successfully");
        verify(tokenBlacklistService).blacklist("abc");
    }

    @Test
    void logout_withoutBearer_doesNotBlacklist() {
        MessageResponse nullAuth = authService.logout(null);
        MessageResponse plain = authService.logout("Token abc");

        assertThat(nullAuth.message()).isEqualTo("Logged out successfully");
        assertThat(plain.message()).isEqualTo("Logged out successfully");
        verify(tokenBlacklistService, never()).blacklist(any());
    }
}
