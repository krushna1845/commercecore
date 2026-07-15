package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {
    
    Optional<ProductRating> findByProductIdAndUserId(Long productId, Long userId);
    
    List<ProductRating> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    @Query("SELECT AVG(r.rating) FROM ProductRating r WHERE r.productId = ?1")
    Double findAverageRatingByProductId(Long productId);
    
    @Query("SELECT COUNT(r) FROM ProductRating r WHERE r.productId = ?1")
    long countByProductId(Long productId);
    
    List<ProductRating> findByUserIdOrderByCreatedAtDesc(Long userId);
}
