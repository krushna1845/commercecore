package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.User;
import com.krushna.commercecore.model.WishlistAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistAnalyticsRepository extends JpaRepository<WishlistAnalytics, Long> {
    Optional<WishlistAnalytics> findByUserAndProductId(User user, Long productId);
    List<WishlistAnalytics> findByUserOrderByViewCountDesc(User user);
    List<WishlistAnalytics> findByUserOrderByMoveToCartCountDesc(User user);
}
