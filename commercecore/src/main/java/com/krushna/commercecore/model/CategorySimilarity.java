package com.krushna.commercecore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "category_similarity",
    indexes = {
        @Index(name = "idx_similarity", columnList = "similarity_score DESC")
    }
)
public class CategorySimilarity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long categoryId1;
    
    @Column(nullable = false)
    private Long categoryId2;
    
    @Column(nullable = false)
    private Double similarityScore;
    
    // Constructors
    public CategorySimilarity() {}
    
    public CategorySimilarity(Long categoryId1, Long categoryId2, Double similarityScore) {
        this.categoryId1 = categoryId1;
        this.categoryId2 = categoryId2;
        this.similarityScore = similarityScore;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCategoryId1() { return categoryId1; }
    public void setCategoryId1(Long categoryId1) { this.categoryId1 = categoryId1; }
    
    public Long getCategoryId2() { return categoryId2; }
    public void setCategoryId2(Long categoryId2) { this.categoryId2 = categoryId2; }
    
    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }
}
