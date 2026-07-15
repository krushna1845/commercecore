package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {
    List<Payout> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<Payout> findByStatusOrderByCreatedAtDesc(Payout.Status status);
}
