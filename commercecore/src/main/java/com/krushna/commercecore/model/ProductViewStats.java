package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_view_stats",
    indexes = {
        @Index(name = "idx_views_24h", columnList = "view_count_24h DESC"),
        @Index(name = "idx_rating", columnList = "avg_rating DESC")
    }
)
public class ProductViewStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long productId;
    
    @Column(name = "view_count_24h", nullable = false)
    private Integer viewCount24h = 0;
    
    @Column(name = "view_count_7d", nullable = false)
    private Integer viewCount7d = 0;
    
    @Column(name = "view_count_30d", nullable = false)
    private Integer viewCount30d = 0;
    
    @Column(name = "avg_rating")
    private Double avgRating;
    
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public ProductViewStats() {}
    
    public ProductViewStats(Long productId) {
        this.productId = productId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Integer getViewCount24h() { return viewCount24h; }
    public void setViewCount24h(Integer count) { this.viewCount24h = count; }
    
    public Integer getViewCount7d() { return viewCount7d; }
    public void setViewCount7d(Integer count) { this.viewCount7d = count; }
    
    public Integer getViewCount30d() { return viewCount30d; }
    public void setViewCount30d(Integer count) { this.viewCount30d = count; }
    
    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
