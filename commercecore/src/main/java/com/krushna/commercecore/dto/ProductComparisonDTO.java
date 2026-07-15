package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductComparisonDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private double price;
    private double originalPrice;
    private double discountPercentage;
    private double rating;
    private int reviewCount;
    private int stockQuantity;
    private String brand;
    private String warranty;
    private String returnPolicy;
    private Map<String, String> specs;
    private List<String> features;
    private boolean bestPrice;
    private boolean bestRating;
    private boolean bestDiscount;
    private boolean inStock;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }
    public String getReturnPolicy() { return returnPolicy; }
    public void setReturnPolicy(String returnPolicy) { this.returnPolicy = returnPolicy; }
    public Map<String, String> getSpecs() { return specs; }
    public void setSpecs(Map<String, String> specs) { this.specs = specs; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    public boolean isBestPrice() { return bestPrice; }
    public void setBestPrice(boolean bestPrice) { this.bestPrice = bestPrice; }
    public boolean isBestRating() { return bestRating; }
    public void setBestRating(boolean bestRating) { this.bestRating = bestRating; }
    public boolean isBestDiscount() { return bestDiscount; }
    public void setBestDiscount(boolean bestDiscount) { this.bestDiscount = bestDiscount; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
}
