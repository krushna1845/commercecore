package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.ProductRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, Long> {
    
    @Query("SELECT p FROM ProductRecommendation p WHERE p.productId = ?1 AND p.recommendationType = ?2 AND (p.expiresAt IS NULL OR p.expiresAt > ?3) ORDER BY p.rankPosition ASC LIMIT ?4")
    List<ProductRecommendation> findByProductIdAndType(Long productId, String type, LocalDateTime now, int limit);
    
    @Query("SELECT p FROM ProductRecommendation p WHERE p.productId = ?1 AND p.recommendationType = ?2 ORDER BY p.score DESC LIMIT ?3")
    List<ProductRecommendation> findTopByProductIdAndType(Long productId, String type, int limit);
    
    void deleteByProductIdAndRecommendationType(Long productId, String type);
    
    @Query("DELETE FROM ProductRecommendation p WHERE p.expiresAt IS NOT NULL AND p.expiresAt <= ?1")
    void deleteExpired(LocalDateTime now);
}
