package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.ForgotPasswordRequestDTO;
import com.krushna.commercecore.dto.LoginRequest;
import com.krushna.commercecore.dto.LoginResponseDTO;
import com.krushna.commercecore.dto.RegisterRequest;
import com.krushna.commercecore.dto.ResetPasswordRequestDTO;
import com.krushna.commercecore.dto.TokenRefreshRequestDTO;
import com.krushna.commercecore.dto.TokenRefreshResponseDTO;
import com.krushna.commercecore.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        service.register(request);
        return ResponseEntity.ok("User registered successfully. Please verify your email.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequest request) {
        LoginResponseDTO response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        LoginResponseDTO dto = service.getMe(authentication.getName());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/become-seller")
    public ResponseEntity<LoginResponseDTO> becomeSeller(
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        if (userDetails == null
                || authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).build();
        }
        LoginResponseDTO response = service.becomeSeller(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refresh(@Valid @RequestBody TokenRefreshRequestDTO request) {
        TokenRefreshResponseDTO response = service.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        service.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset email sent successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        service.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully.");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        service.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<LoginResponseDTO> verifyMfa(
            @RequestParam String username,
            @RequestParam String tempToken,
            @RequestParam String code) {
        LoginResponseDTO response = service.verifyMfa(username, tempToken, code);
        return ResponseEntity.ok(response);
    }
}
