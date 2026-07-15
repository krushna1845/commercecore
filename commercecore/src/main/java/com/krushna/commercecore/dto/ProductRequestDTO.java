package com.krushna.commercecore.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductRequestDTO {

    @NotBlank(message = "Product name cannot be empty")
    private String name;

    private String description;

    private String sku;
    private double weight;

    @Positive(message = "Price must be greater than 0")
    private double price;

    private double originalPrice;

    private String imageUrl;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    private String brand;
    private String warranty;
    private String returnPolicy;
    private double rating;
    private int reviewCount;
    private List<String> features = new ArrayList<>();
    private Map<String, String> specs = new HashMap<>();
    private Long categoryId;
    private Long sellerId;

    public ProductRequestDTO() {}

    public ProductRequestDTO(String name, String description, double price, double originalPrice, String imageUrl, int stockQuantity,
                             String brand, String warranty, String returnPolicy, double rating, int reviewCount,
                             List<String> features, Map<String, String> specs, Long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.brand = brand;
        this.warranty = warranty;
        this.returnPolicy = returnPolicy;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.features = features != null ? features : new ArrayList<>();
        this.specs = specs != null ? specs : new HashMap<>();
        this.categoryId = categoryId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }

    public String getReturnPolicy() { return returnPolicy; }
    public void setReturnPolicy(String returnPolicy) { this.returnPolicy = returnPolicy; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public Map<String, String> getSpecs() { return specs; }
    public void setSpecs(Map<String, String> specs) { this.specs = specs; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
