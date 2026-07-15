package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_sales_frequency",
    indexes = {
        @Index(name = "idx_sales_24h", columnList = "sales_count_24h DESC"),
        @Index(name = "idx_sales_7d", columnList = "sales_count_7d DESC"),
        @Index(name = "idx_sales_30d", columnList = "sales_count_30d DESC")
    }
)
public class ProductSalesFrequency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long productId;
    
    @Column(name = "sales_count_24h", nullable = false)
    private Integer salesCount24h = 0;
    
    @Column(name = "sales_count_7d", nullable = false)
    private Integer salesCount7d = 0;
    
    @Column(name = "sales_count_30d", nullable = false)
    private Integer salesCount30d = 0;
    
    @Column(name = "last_sale_at")
    private LocalDateTime lastSaleAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public ProductSalesFrequency() {}
    
    public ProductSalesFrequency(Long productId) {
        this.productId = productId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Integer getSalesCount24h() { return salesCount24h; }
    public void setSalesCount24h(Integer count) { this.salesCount24h = count; }
    
    public Integer getSalesCount7d() { return salesCount7d; }
    public void setSalesCount7d(Integer count) { this.salesCount7d = count; }
    
    public Integer getSalesCount30d() { return salesCount30d; }
    public void setSalesCount30d(Integer count) { this.salesCount30d = count; }
    
    public LocalDateTime getLastSaleAt() { return lastSaleAt; }
    public void setLastSaleAt(LocalDateTime lastSaleAt) { this.lastSaleAt = lastSaleAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
