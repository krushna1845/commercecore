package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.CategorySimilarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategorySimilarityRepository extends JpaRepository<CategorySimilarity, Long> {
    
    @Query("SELECT c FROM CategorySimilarity c WHERE (c.categoryId1 = ?1 OR c.categoryId2 = ?1) ORDER BY c.similarityScore DESC LIMIT ?2")
    List<CategorySimilarity> findSimilarCategories(Long categoryId, int limit);
    
    Optional<CategorySimilarity> findByCategoryId1AndCategoryId2(Long categoryId1, Long categoryId2);
}
