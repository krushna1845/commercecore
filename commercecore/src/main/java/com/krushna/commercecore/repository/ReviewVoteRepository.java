package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Review;
import com.krushna.commercecore.model.ReviewVote;
import com.krushna.commercecore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {
    Optional<ReviewVote> findByReviewAndUser(Review review, User user);
    int countByReviewAndIsHelpfulTrue(Review review);
    int countByReviewAndIsHelpfulFalse(Review review);
    void deleteByReviewAndUser(Review review, User user);
}
