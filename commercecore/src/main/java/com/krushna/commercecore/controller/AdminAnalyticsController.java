package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.AdminAnalyticsDTO;
import com.krushna.commercecore.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Analytics", description = "Admin dashboard analytics APIs")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping
    @Operation(summary = "Get comprehensive admin analytics")
    public ResponseEntity<AdminAnalyticsDTO> getAnalytics() {
        AdminAnalyticsDTO analytics = adminAnalyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
