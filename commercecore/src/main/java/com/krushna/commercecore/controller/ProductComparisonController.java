package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.ComparisonResultDTO;
import com.krushna.commercecore.service.ProductComparisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comparison")
@RequiredArgsConstructor
@Tag(name = "Product Comparison", description = "Product comparison management APIs")
public class ProductComparisonController {

    private final ProductComparisonService comparisonService;

    @PostMapping("/add/{productId}")
    @Operation(summary = "Add product to comparison")
    public ResponseEntity<?> addToComparison(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            comparisonService.addToComparison(userId, productId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "Remove product from comparison")
    public ResponseEntity<Void> removeFromComparison(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        comparisonService.removeFromComparison(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear all products from comparison")
    public ResponseEntity<Void> clearComparison(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        comparisonService.clearComparison(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get comparison list")
    public ResponseEntity<ComparisonResultDTO> getComparison(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(comparisonService.getComparison(userId));
    }
}
