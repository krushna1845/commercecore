package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Review;
import com.krushna.commercecore.model.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReview(Review review);
    void deleteByReview(Review review);
}
