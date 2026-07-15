package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.ProductViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductViewStatsRepository extends JpaRepository<ProductViewStats, Long> {
    
    Optional<ProductViewStats> findByProductId(Long productId);
    
    @Query("SELECT p FROM ProductViewStats p ORDER BY p.viewCount24h DESC LIMIT ?1")
    List<ProductViewStats> findMostViewed24h(int limit);
    
    @Query("SELECT p FROM ProductViewStats p ORDER BY p.avgRating DESC LIMIT ?1")
    List<ProductViewStats> findHighestRated(int limit);
    
    @Query("SELECT p FROM ProductViewStats p ORDER BY p.reviewCount DESC LIMIT ?1")
    List<ProductViewStats> findMostReviewed(int limit);
}
