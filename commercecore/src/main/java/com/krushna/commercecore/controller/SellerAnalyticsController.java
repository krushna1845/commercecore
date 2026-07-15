package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.SellerAnalyticsDTO;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.service.SellerAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
@Tag(name = "Seller Analytics", description = "Seller dashboard analytics APIs")
public class SellerAnalyticsController {

    private final SellerAnalyticsService sellerAnalyticsService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get seller analytics")
    public ResponseEntity<SellerAnalyticsDTO> getSellerAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {
        User seller = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        SellerAnalyticsDTO analytics = sellerAnalyticsService.getSellerAnalytics(seller.getId());
        return ResponseEntity.ok(analytics);
    }
}
