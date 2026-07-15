package com.krushna.commercecore.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist")
public class Wishlist {

    @EmbeddedId
    private WishlistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        this.addedAt = LocalDateTime.now();
    }

    public Wishlist() {}

    public Wishlist(User user, Product product) {
        this.user = user;
        this.product = product;
        this.id = new WishlistId(user.getId(), product.getId());
    }

    // Getters and Setters
    public WishlistId getId() { return id; }
    public void setId(WishlistId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { 
        this.user = user;
        if (user != null && product != null) {
            this.id = new WishlistId(user.getId(), product.getId());
        }
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { 
        this.product = product;
        if (user != null && product != null) {
            this.id = new WishlistId(user.getId(), product.getId());
        }
    }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    @Embeddable
    public static class WishlistId {
        private Long userId;
        private Long productId;

        public WishlistId() {}

        public WishlistId(Long userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WishlistId that = (WishlistId) o;
            return userId.equals(that.userId) && productId.equals(that.productId);
        }

        @Override
        public int hashCode() {
            return userId.hashCode() + productId.hashCode();
        }
    }
}
