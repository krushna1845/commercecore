package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.isActive = true AND c.expiryDate > :now AND (c.usageLimit = 0 OR c.timesUsed < c.usageLimit)")
    Optional<Coupon> findValidCouponByCode(@Param("code") String code, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.expiryDate > :now AND (c.usageLimit = 0 OR c.timesUsed < c.usageLimit)")
    List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true")
    List<Coupon> findActiveCoupons();

    @Query("SELECT c FROM Coupon c WHERE c.expiryDate <= :now OR NOT c.isActive")
    List<Coupon> findExpiredOrInactiveCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.usageLimit > 0 AND c.timesUsed >= c.usageLimit")
    List<Coupon> findFullyUsedCoupons();
}
