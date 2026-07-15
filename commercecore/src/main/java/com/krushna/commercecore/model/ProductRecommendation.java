package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_recommendations",
    indexes = {
        @Index(name = "idx_product_type", columnList = "product_id, recommendation_type"),
        @Index(name = "idx_expires", columnList = "expires_at")
    }
)
public class ProductRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false, length = 50)
    private String recommendationType;
    
    @Column(nullable = false)
    private Long recommendedProductId;
    
    @Column(nullable = false)
    private Double score;
    
    @Column(nullable = false)
    private Integer rankPosition;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    // Constructors
    public ProductRecommendation() {}
    
    public ProductRecommendation(Long productId, String recommendationType, Long recommendedProductId, Double score, Integer rankPosition) {
        this.productId = productId;
        this.recommendationType = recommendationType;
        this.recommendedProductId = recommendedProductId;
        this.score = score;
        this.rankPosition = rankPosition;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    
    public Long getRecommendedProductId() { return recommendedProductId; }
    public void setRecommendedProductId(Long recommendedProductId) { this.recommendedProductId = recommendedProductId; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public Integer getRankPosition() { return rankPosition; }
    public void setRankPosition(Integer rankPosition) { this.rankPosition = rankPosition; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
