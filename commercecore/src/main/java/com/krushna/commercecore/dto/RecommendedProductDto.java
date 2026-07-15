package com.krushna.commercecore.dto;

public class RecommendedProductDto {
    private Long productId;
    private String productName;
    private String description;
    private Double price;
    private Double rating;
    private Integer reviewCount;
    private Double score;
    private Integer rankPosition;
    private String imageUrl;
    
    public RecommendedProductDto() {}
    
    public RecommendedProductDto(Long productId, String productName, Double price, Double rating) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.rating = rating;
    }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public Integer getRankPosition() { return rankPosition; }
    public void setRankPosition(Integer rankPosition) { this.rankPosition = rankPosition; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
