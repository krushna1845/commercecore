package com.krushna.commercecore.dto;

import java.util.List;
import java.util.Map;

public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String sku;
    private double weight;
    private double price;
    private double originalPrice;
    private String imageUrl;
    private int stockQuantity;
    private boolean inStock;
    private String brand;
    private String warranty;
    private String returnPolicy;
    private double rating;
    private int reviewCount;
    private List<String> features;
    private Map<String, String> specs;
    private CategoryResponseDTO category;
    private SellerInfo seller;
    private String approvalStatus;

    public ProductResponseDTO() {}

    public static class SellerInfo {
        private Long id;
        private String username;

        public SellerInfo() {}

        public SellerInfo(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    public ProductResponseDTO(Long id, String name, String description, double price, double originalPrice, String imageUrl,
                              int stockQuantity, boolean inStock, String brand, String warranty, String returnPolicy,
                              double rating, int reviewCount, List<String> features, Map<String, String> specs,
                              CategoryResponseDTO category, SellerInfo seller, String approvalStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.inStock = inStock;
        this.brand = brand;
        this.warranty = warranty;
        this.returnPolicy = returnPolicy;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.features = features;
        this.specs = specs;
        this.category = category;
        this.seller = seller;
        this.approvalStatus = approvalStatus;
    }

    public ProductResponseDTO(Long id, String name, String description, double price, double originalPrice, String imageUrl,
                              int stockQuantity, boolean inStock, String brand, String warranty, String returnPolicy,
                              double rating, int reviewCount, List<String> features, Map<String, String> specs,
                              CategoryResponseDTO category, SellerInfo seller) {
        this(id, name, description, price, originalPrice, imageUrl, stockQuantity, inStock,
                brand, warranty, returnPolicy, rating, reviewCount, features, specs, category, seller, "PENDING");
    }

    public ProductResponseDTO(Long id, String name, String description, double price, String imageUrl,
                              int stockQuantity, boolean inStock, CategoryResponseDTO category) {
        this(id, name, description, price, price, imageUrl, stockQuantity, inStock,
                "", "", "", 0.0, 0, List.of(), Map.of(), category, null, "PENDING");
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }

    public String getReturnPolicy() { return returnPolicy; }
    public void setReturnPolicy(String returnPolicy) { this.returnPolicy = returnPolicy; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public Map<String, String> getSpecs() { return specs; }
    public void setSpecs(Map<String, String> specs) { this.specs = specs; }

    public CategoryResponseDTO getCategory() { return category; }
    public void setCategory(CategoryResponseDTO category) { this.category = category; }

    public SellerInfo getSeller() { return seller; }
    public void setSeller(SellerInfo seller) { this.seller = seller; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
