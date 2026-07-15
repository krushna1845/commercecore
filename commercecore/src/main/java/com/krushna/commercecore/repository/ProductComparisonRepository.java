package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.ProductComparison;
import com.krushna.commercecore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductComparisonRepository extends JpaRepository<ProductComparison, Long> {
    List<ProductComparison> findByUserOrderByAddedAtDesc(User user);
    Optional<ProductComparison> findByUserAndProductId(User user, Long productId);
    void deleteByUser(User user);
    void deleteByUserAndProductId(User user, Long productId);
    int countByUser(User user);
}
