package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.ProductSalesFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSalesFrequencyRepository extends JpaRepository<ProductSalesFrequency, Long> {
    
    Optional<ProductSalesFrequency> findByProductId(Long productId);
    
    @Query("SELECT p FROM ProductSalesFrequency p ORDER BY p.salesCount24h DESC LIMIT ?1")
    List<ProductSalesFrequency> findTrendingProducts(int limit);
    
    @Query("SELECT p FROM ProductSalesFrequency p ORDER BY p.salesCount7d DESC LIMIT ?1")
    List<ProductSalesFrequency> findPopularProducts7d(int limit);
    
    @Query("SELECT p FROM ProductSalesFrequency p ORDER BY p.salesCount30d DESC LIMIT ?1")
    List<ProductSalesFrequency> findPopularProducts30d(int limit);
}
