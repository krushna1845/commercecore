package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.ReviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewSummaryRepository extends JpaRepository<ReviewSummary, Long> {
    Optional<ReviewSummary> findByProduct(Product product);
    void deleteByProduct(Product product);
}
