package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "browsing_history",
    indexes = {
        @Index(name = "idx_user_timestamp", columnList = "user_id, viewed_at DESC"),
        @Index(name = "idx_product_timestamp", columnList = "product_id, viewed_at DESC")
    }
)
public class BrowsingHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt = LocalDateTime.now();
    
    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds = 0;
    
    // Constructors
    public BrowsingHistory() {}
    
    public BrowsingHistory(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public LocalDateTime getViewedAt() { return viewedAt; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }
    
    public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
}
