package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.RatingDistributionDTO;
import com.krushna.commercecore.dto.ReviewDetailDTO;
import com.krushna.commercecore.dto.ReviewSummaryDTO;
import com.krushna.commercecore.service.EnhancedReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews/enhanced")
@RequiredArgsConstructor
@Tag(name = "Enhanced Reviews", description = "Enhanced review management APIs")
public class EnhancedReviewController {

    private final EnhancedReviewService reviewService;

    @PostMapping
    @Operation(summary = "Add a new review with images and videos")
    public ResponseEntity<ReviewDetailDTO> addReview(
            @RequestParam Long productId,
            @RequestParam int rating,
            @RequestParam String comment,
            @RequestParam(required = false) List<String> imageUrls,
            @RequestParam(required = false) List<Map<String, String>> videoData,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(reviewService.addReview(userId, productId, rating, comment, imageUrls, videoData));
    }

    @PostMapping("/{reviewId}/vote")
    @Operation(summary = "Vote on a review")
    public ResponseEntity<Void> voteReview(
            @PathVariable Long reviewId,
            @RequestParam boolean isHelpful,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        reviewService.voteReview(userId, reviewId, isHelpful);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}/vote")
    @Operation(summary = "Remove vote from a review")
    public ResponseEntity<Void> removeVote(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        reviewService.removeVote(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/replies")
    @Operation(summary = "Add a reply to a review")
    public ResponseEntity<Void> addReply(
            @PathVariable Long reviewId,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        reviewService.addReply(userId, reviewId, content);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/replies/{replyId}")
    @Operation(summary = "Update a reply")
    public ResponseEntity<Void> updateReply(
            @PathVariable Long replyId,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        reviewService.updateReply(userId, replyId, content);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/replies/{replyId}")
    @Operation(summary = "Delete a reply")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        reviewService.deleteReply(userId, replyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review details")
    public ResponseEntity<ReviewDetailDTO> getReviewDetail(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(reviewService.getReviewDetail(reviewId, userId));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product reviews with filtering and sorting")
    public ResponseEntity<List<ReviewDetailDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "recent") String sortBy,
            @RequestParam(defaultValue = "all") String filter,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(reviewService.getProductReviews(productId, userId, sortBy, filter));
    }

    @GetMapping("/product/{productId}/distribution")
    @Operation(summary = "Get rating distribution for a product")
    public ResponseEntity<RatingDistributionDTO> getRatingDistribution(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getRatingDistribution(productId));
    }

    @PostMapping("/product/{productId}/summary")
    @Operation(summary = "Generate AI review summary for a product")
    public ResponseEntity<ReviewSummaryDTO> generateReviewSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.generateReviewSummary(productId));
    }

    @GetMapping("/product/{productId}/summary")
    @Operation(summary = "Get existing review summary for a product")
    public ResponseEntity<ReviewSummaryDTO> getReviewSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewSummary(productId));
    }
}
