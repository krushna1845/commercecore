package com.krushna.commercecore.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.krushna.commercecore.dto.LoginResponseDTO;

import com.krushna.commercecore.dto.LoginRequest;
import com.krushna.commercecore.dto.RegisterRequest;
import com.krushna.commercecore.model.Role;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.model.RefreshToken;
import com.krushna.commercecore.repository.RoleRepository;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MfaService mfaService;

    @Mock
    private EmailService emailService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;
    private Role sampleRole;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUsername("testuser");
        sampleUser.setPassword("$2a$10$encodedHashedPassword");

        sampleRole = new Role();
        sampleRole.setName("ROLE_USER");
    }

    // ─────────────────────── register() ────────────────────────────────────

    @Test
    @DisplayName("register() - should save user with encoded password")
    void register_shouldSaveUserWithEncodedPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("rawPassword");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(sampleRole));
        when(passwordEncoder.encode("rawPassword")).thenReturn("$2a$10$hashedValue");

        authService.register(request);

        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(argThat(user ->
                "newuser".equals(user.getUsername()) &&
                "$2a$10$hashedValue".equals(user.getPassword())
        ));
    }

    @Test
    @DisplayName("register() - should throw exception when username already exists")
    void register_shouldThrow_whenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository, never()).save(any());
    }

    // ─────────────────────── login() ────────────────────────────────────────

    @Test
    @DisplayName("login() - should return JWT token for valid credentials")
    void login_shouldReturnJwtToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("correctPassword");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mock.refresh.token");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("correctPassword", sampleUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "ROLE_USER")).thenReturn("mock.jwt.token");
        when(refreshTokenService.createRefreshToken("testuser")).thenReturn(mockRefreshToken);

        LoginResponseDTO response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getRefreshToken()).isEqualTo("mock.refresh.token");
        verify(jwtUtil, times(1)).generateToken("testuser", "ROLE_USER");
    }

    @Test
    @DisplayName("login() - should throw exception when user is not found")
    void login_shouldThrow_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknownuser");
        request.setPassword("somePassword");

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid username or password");

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("login() - should throw exception when password is incorrect")
    void login_shouldThrow_whenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPassword", sampleUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid username or password");

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("login() - should not encode password during login (only matching)")
    void login_shouldNotEncodePassword_duringLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("correctPassword");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mock.refresh.token");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");
        when(refreshTokenService.createRefreshToken("testuser")).thenReturn(mockRefreshToken);

        authService.login(request);

        verify(passwordEncoder, never()).encode(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
    }
}
