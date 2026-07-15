package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_analytics")
public class WishlistAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int viewCount;
    private int moveToCartCount;
    private LocalDateTime lastViewedAt;
    private LocalDateTime lastMovedToCartAt;

    @PrePersist
    protected void onCreate() {
        this.viewCount = 0;
        this.moveToCartCount = 0;
    }

    public WishlistAnalytics() {}

    public WishlistAnalytics(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getMoveToCartCount() { return moveToCartCount; }
    public void setMoveToCartCount(int moveToCartCount) { this.moveToCartCount = moveToCartCount; }

    public LocalDateTime getLastViewedAt() { return lastViewedAt; }
    public void setLastViewedAt(LocalDateTime lastViewedAt) { this.lastViewedAt = lastViewedAt; }

    public LocalDateTime getLastMovedToCartAt() { return lastMovedToCartAt; }
    public void setLastMovedToCartAt(LocalDateTime lastMovedToCartAt) { this.lastMovedToCartAt = lastMovedToCartAt; }

    public void incrementViewCount() {
        this.viewCount++;
        this.lastViewedAt = LocalDateTime.now();
    }

    public void incrementMoveToCartCount() {
        this.moveToCartCount++;
        this.lastMovedToCartAt = LocalDateTime.now();
    }
}
