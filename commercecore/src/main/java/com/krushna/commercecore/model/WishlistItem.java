package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items")
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private WishlistFolder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    private boolean priceDropAlert;
    private boolean stockAlert;
    private double alertPrice;

    @PrePersist
    protected void onCreate() {
        this.addedAt = LocalDateTime.now();
    }

    public WishlistItem() {}

    public WishlistItem(WishlistFolder folder, Product product) {
        this.folder = folder;
        this.product = product;
        this.priceDropAlert = true;
        this.stockAlert = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WishlistFolder getFolder() { return folder; }
    public void setFolder(WishlistFolder folder) { this.folder = folder; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public boolean isPriceDropAlert() { return priceDropAlert; }
    public void setPriceDropAlert(boolean priceDropAlert) { this.priceDropAlert = priceDropAlert; }

    public boolean isStockAlert() { return stockAlert; }
    public void setStockAlert(boolean stockAlert) { this.stockAlert = stockAlert; }

    public double getAlertPrice() { return alertPrice; }
    public void setAlertPrice(double alertPrice) { this.alertPrice = alertPrice; }
}
