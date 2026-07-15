package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.ReviewRequestDTO;
import com.krushna.commercecore.dto.ReviewResponseDTO;
import com.krushna.commercecore.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponseDTO created = reviewService.createReview(userDetails.getUsername(), request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByProduct(@PathVariable Long productId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(@AuthenticationPrincipal UserDetails userDetails) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByUser(userDetails.getUsername());
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponseDTO updated = reviewService.updateReview(reviewId, userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        Double averageRating = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> getReviewCount(@PathVariable Long productId) {
        Long count = reviewService.getReviewCount(productId);
        return ResponseEntity.ok(count);
    }
}
