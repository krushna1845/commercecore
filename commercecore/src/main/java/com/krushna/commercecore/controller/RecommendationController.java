package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.MultipleRecommendationsResponse;
import com.krushna.commercecore.dto.RecommendationResponse;
import com.krushna.commercecore.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    // Product-based recommendations
    @GetMapping("/product/{productId}/frequently-bought-together")
    public ResponseEntity<RecommendationResponse> getFrequentlyBoughtTogether(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getFrequentlyBoughtTogether(productId, limit));
    }
    
    @GetMapping("/product/{productId}/customers-also-bought")
    public ResponseEntity<RecommendationResponse> getCustomersAlsoBought(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getCustomersAlsoBought(productId, limit));
    }
    
    // User-based recommendations
    @GetMapping("/user/{userId}/recommended-for-you")
    public ResponseEntity<RecommendationResponse> getRecommendedForYou(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getRecommendedForYou(userId, limit));
    }
    
    // Global trending and popular recommendations
    @GetMapping("/trending")
    public ResponseEntity<RecommendationResponse> getTrendingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getTrendingProducts(limit));
    }
    
    @GetMapping("/popular")
    public ResponseEntity<RecommendationResponse> getPopularProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getPopularProducts(limit));
    }
    
    // User browsing history based
    @GetMapping("/user/{userId}/recently-viewed")
    public ResponseEntity<RecommendationResponse> getRecentlyViewed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getRecentlyViewed(userId, limit));
    }
    
    // Get all recommendations for a product
    @GetMapping("/product/{productId}/all")
    public ResponseEntity<MultipleRecommendationsResponse> getAllProductRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<RecommendationResponse> recommendations = new ArrayList<>();
        recommendations.add(recommendationService.getFrequentlyBoughtTogether(productId, limit));
        recommendations.add(recommendationService.getCustomersAlsoBought(productId, limit));
        
        return ResponseEntity.ok(new MultipleRecommendationsResponse(productId, recommendations));
    }
    
    // Record user actions for recommendation training
    @PostMapping("/user/{userId}/track-browse/{productId}")
    public ResponseEntity<?> trackBrowse(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        recommendationService.recordBrowsing(userId, productId);
        return ResponseEntity.ok("Browse recorded");
    }
}
