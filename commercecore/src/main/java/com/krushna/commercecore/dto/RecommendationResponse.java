package com.krushna.commercecore.dto;

import java.util.List;

public class RecommendationResponse {
    private String recommendationType;
    private List<RecommendedProductDto> products;
    private long cachedAt;
    
    public RecommendationResponse() {}
    
    public RecommendationResponse(String recommendationType, List<RecommendedProductDto> products) {
        this.recommendationType = recommendationType;
        this.products = products;
        this.cachedAt = System.currentTimeMillis();
    }
    
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    
    public List<RecommendedProductDto> getProducts() { return products; }
    public void setProducts(List<RecommendedProductDto> products) { this.products = products; }
    
    public long getCachedAt() { return cachedAt; }
    public void setCachedAt(long cachedAt) { this.cachedAt = cachedAt; }
}
