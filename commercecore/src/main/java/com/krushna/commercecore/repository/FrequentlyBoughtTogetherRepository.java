package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.FrequentlyBoughtTogether;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FrequentlyBoughtTogetherRepository extends JpaRepository<FrequentlyBoughtTogether, Long> {
    
    @Query("SELECT f FROM FrequentlyBoughtTogether f WHERE f.productId = ?1 ORDER BY f.confidenceScore DESC LIMIT ?2")
    List<FrequentlyBoughtTogether> findTopByProductId(Long productId, int limit);
    
    Optional<FrequentlyBoughtTogether> findByProductIdAndRelatedProductId(Long productId, Long relatedProductId);
    
    List<FrequentlyBoughtTogether> findByProductIdOrderByConfidenceScoreDesc(Long productId);
}
