package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Review;
import com.krushna.commercecore.model.ReviewVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewVideoRepository extends JpaRepository<ReviewVideo, Long> {
    List<ReviewVideo> findByReview(Review review);
    void deleteByReview(Review review);
}
