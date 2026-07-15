package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.AddressResponseDTO;
import com.krushna.commercecore.dto.MfaSetupDTO;
import com.krushna.commercecore.dto.UserProfileDTO;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.service.AddressService;
import com.krushna.commercecore.service.CloudinaryService;
import com.krushna.commercecore.service.MfaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final CloudinaryService cloudinaryService;
    private final MfaService mfaService;

    public UserProfileController(UserRepository userRepository,
                                 AddressService addressService,
                                 CloudinaryService cloudinaryService,
                                 MfaService mfaService) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.cloudinaryService = cloudinaryService;
        this.mfaService = mfaService;
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO dto = convertToUserProfileDTO(user);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestBody UserProfileDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getLanguage() != null) {
            user.setLanguage(request.getLanguage());
        }
        if (request.getCurrency() != null) {
            user.setCurrency(request.getCurrency());
        }
        user.setEmailNotifications(request.isEmailNotifications());
        user.setSmsNotifications(request.isSmsNotifications());
        user.setPushNotifications(request.isPushNotifications());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(convertToUserProfileDTO(updatedUser));
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = cloudinaryService.uploadImage(file);
        user.setProfilePictureUrl(imageUrl);
        userRepository.save(user);

        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<MfaSetupDTO> setupMfa(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret = mfaService.generateSecretKey();
        user.setMfaSecret(secret);
        userRepository.save(user);

        String qrCodeUrl = mfaService.getQrCodeUrl(user.getUsername(), secret);
        return ResponseEntity.ok(new MfaSetupDTO(secret, qrCodeUrl));
    }

    @PostMapping("/mfa/enable")
    public ResponseEntity<String> enableMfa(
            @RequestParam String code,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getMfaSecret() == null) {
            return ResponseEntity.badRequest().body("MFA secret not set. Initiate setup first.");
        }

        boolean isValid = mfaService.verifyCode(user.getMfaSecret(), code);
        if (!isValid) {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }

        user.setMfaEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok("MFA enabled successfully.");
    }

    @PostMapping("/mfa/disable")
    public ResponseEntity<String> disableMfa(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
        return ResponseEntity.ok("MFA disabled successfully.");
    }

    private UserProfileDTO convertToUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setFullName(user.getFullName());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setLanguage(user.getLanguage());
        dto.setCurrency(user.getCurrency());
        dto.setEmailNotifications(user.isEmailNotifications());
        dto.setSmsNotifications(user.isSmsNotifications());
        dto.setPushNotifications(user.isPushNotifications());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setPhoneVerified(user.isPhoneVerified());
        dto.setMfaEnabled(user.isMfaEnabled());
        dto.setRoles(user.getRoles().stream().map(r -> r.getName()).toList());

        List<AddressResponseDTO> addresses = addressService.getUserAddresses(user.getUsername());
        dto.setAddresses(addresses);
        return dto;
    }
}
