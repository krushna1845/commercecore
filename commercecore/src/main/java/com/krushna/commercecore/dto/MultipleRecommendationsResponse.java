package com.krushna.commercecore.dto;

import java.util.List;

public class MultipleRecommendationsResponse {
    private Long productId;
    private List<RecommendationResponse> recommendations;
    
    public MultipleRecommendationsResponse() {}
    
    public MultipleRecommendationsResponse(Long productId, List<RecommendationResponse> recommendations) {
        this.productId = productId;
        this.recommendations = recommendations;
    }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public List<RecommendationResponse> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationResponse> recommendations) { this.recommendations = recommendations; }
}
