package com.krushna.commercecore.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.krushna.commercecore.dto.LoginRequest;
import com.krushna.commercecore.dto.LoginResponseDTO;
import com.krushna.commercecore.dto.RegisterRequest;
import com.krushna.commercecore.dto.TokenRefreshRequestDTO;
import com.krushna.commercecore.dto.TokenRefreshResponseDTO;
import com.krushna.commercecore.model.Role;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.model.RefreshToken;
import com.krushna.commercecore.repository.RoleRepository;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.security.JwtUtil;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final MfaService mfaService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository repository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       MfaService mfaService,
                       EmailService emailService,
                       RefreshTokenService refreshTokenService) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.mfaService = mfaService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        if (request.getEmail() != null && repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        Role userRole = roleRepository.findByName(Role.PredefinedRole.ROLE_USER.getRoleName())
                .orElseThrow(() -> new RuntimeException("Default ROLE_USER not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.addRole(userRole);

        if (request.getEmail() != null) {
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            emailService.sendVerificationEmail(request.getEmail(), token);
        }

        repository.save(user);
    }

    public LoginResponseDTO login(LoginRequest request) {
        log.info("Login attempt for username={}", request != null ? request.getUsername() : null);

        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (user.getPassword() == null || !encoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        // Check if MFA is enabled
        if (user.isMfaEnabled()) {
            log.info("User {} requires MFA verification", user.getUsername());
            String tempToken = jwtUtil.generateTempToken(user.getUsername());
            return new LoginResponseDTO(true, tempToken, user.getUsername());
        }

        String role = determineUserRole(user);
        String token = jwtUtil.generateToken(user.getUsername(), role);
        String refreshToken = refreshTokenService.createRefreshToken(user.getUsername()).getToken();
        
        return new LoginResponseDTO(token, refreshToken, user.getUsername(), role);
    }

    public LoginResponseDTO verifyMfa(String username, String tempToken, String code) {
        if (!jwtUtil.isTokenValid(tempToken, username)) {
            throw new BadCredentialsException("Invalid temp token");
        }
        String role = jwtUtil.extractRole(tempToken);
        if (!"ROLE_PRE_AUTH".equals(role)) {
            throw new BadCredentialsException("Invalid token scope");
        }
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!mfaService.verifyCode(user.getMfaSecret(), code)) {
            throw new BadCredentialsException("Invalid MFA verification code");
        }

        String finalRole = determineUserRole(user);
        String token = jwtUtil.generateToken(user.getUsername(), finalRole);
        String refreshToken = refreshTokenService.createRefreshToken(user.getUsername()).getToken();
        
        return new LoginResponseDTO(token, refreshToken, user.getUsername(), finalRole);
    }

    private String determineUserRole(User user) {
        if (user.isAdmin()) {
            return "ROLE_ADMIN";
        } else if (user.isSeller()) {
            return "ROLE_SELLER";
        } else if (user.isSupportAgent()) {
            return "ROLE_SUPPORT_AGENT";
        } else if (user.isDeliveryPartner()) {
            return "ROLE_DELIVERY_PARTNER";
        } else {
            return "ROLE_USER";
        }
    }

    public LoginResponseDTO getMe(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String role = determineUserRole(user);
        
        // Return without token (just user info)
        return new LoginResponseDTO(null, null, user.getUsername(), role);
    }

    @Transactional
    public LoginResponseDTO becomeSeller(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isAdmin()) {
            throw new RuntimeException("Administrators cannot become sellers");
        }
        if (user.isSeller()) {
            throw new RuntimeException("You are already a seller");
        }

        Role sellerRole = roleRepository.findByName(Role.PredefinedRole.ROLE_SELLER.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(Role.PredefinedRole.ROLE_SELLER.getRoleName())));
        user.addRole(sellerRole);
        repository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), "ROLE_SELLER");
        String refreshToken = refreshTokenService.createRefreshToken(user.getUsername()).getToken();
        return new LoginResponseDTO(token, refreshToken, user.getUsername(), "ROLE_SELLER");
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        repository.save(user);

        emailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = repository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getResetPasswordTokenExpiry() == null || user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        repository.save(user);
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = repository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        repository.save(user);
    }

    @Transactional
    public void verifyPhone(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPhoneVerified(true);
        repository.save(user);
    }

    public TokenRefreshResponseDTO refreshAccessToken(TokenRefreshRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();
        RefreshToken token = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        refreshTokenService.verifyExpiration(token);
        User user = token.getUser();
        String role = determineUserRole(user);
        String accessToken = jwtUtil.generateToken(user.getUsername(), role);
        return new TokenRefreshResponseDTO(accessToken, requestRefreshToken);
    }
}
