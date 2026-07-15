package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Review;
import com.krushna.commercecore.model.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    List<ReviewReply> findByReviewOrderByCreatedAtAsc(Review review);
    void deleteByReview(Review review);
}
