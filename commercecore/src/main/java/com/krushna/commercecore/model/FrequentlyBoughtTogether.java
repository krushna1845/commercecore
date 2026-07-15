package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "frequently_bought_together", 
    indexes = {
        @Index(name = "idx_product", columnList = "product_id"),
        @Index(name = "idx_confidence", columnList = "confidence_score DESC")
    }
)
public class FrequentlyBoughtTogether {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Long relatedProductId;
    
    @Column(name = "purchase_count", nullable = false)
    private Integer purchaseCount = 1;
    
    @Column(name = "confidence_score")
    private Double confidenceScore = 0.0;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public FrequentlyBoughtTogether() {}
    
    public FrequentlyBoughtTogether(Long productId, Long relatedProductId) {
        this.productId = productId;
        this.relatedProductId = relatedProductId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Long getRelatedProductId() { return relatedProductId; }
    public void setRelatedProductId(Long relatedProductId) { this.relatedProductId = relatedProductId; }
    
    public Integer getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(Integer purchaseCount) { this.purchaseCount = purchaseCount; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
