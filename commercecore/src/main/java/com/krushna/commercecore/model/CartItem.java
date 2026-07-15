package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    // FK to the User entity (Nullable for guest carts)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "guest_id")
    private String guestId;

    // FK to the Product entity
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public CartItem() {}

    public CartItem(User user, String guestId, Product product, int quantity) {
        this.user = user;
        this.guestId = guestId;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() { return id; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /** Computes the line-item subtotal using BigDecimal for currency safety. */
    public BigDecimal getSubtotal() {
        return BigDecimal.valueOf(product.getPrice())
                .multiply(BigDecimal.valueOf(quantity));
    }
}
