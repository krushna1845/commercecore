package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.CouponRequestDTO;
import com.krushna.commercecore.dto.CouponResponseDTO;
import com.krushna.commercecore.model.Coupon;
import com.krushna.commercecore.repository.CouponRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponResponseDTO createCoupon(CouponRequestDTO request) {
        // Check if coupon code already exists
        if (couponRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }

        Coupon coupon = new Coupon(request.getCode(), 
                                 com.krushna.commercecore.model.DiscountType.valueOf(request.getDiscountType()),
                                 request.getDiscountPercentage(), 
                                 request.getDiscountAmount(),
                                 request.getExpiryDate());
        coupon.setMinPurchaseAmount(request.getMinPurchaseAmount());
        coupon.setActive(request.isActive());
        coupon.setUsageLimit(request.getUsageLimit());

        Coupon saved = couponRepository.save(coupon);
        return convertToDTO(saved);
    }

    public List<CouponResponseDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CouponResponseDTO> getActiveCoupons() {
        List<Coupon> coupons = couponRepository.findActiveCoupons(LocalDateTime.now());
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CouponResponseDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found: " + code));
        return convertToDTO(coupon);
    }

    public CouponResponseDTO validateCoupon(String code) {
        Coupon coupon = couponRepository.findValidCouponByCode(code.toUpperCase(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired coupon"));
        return convertToDTO(coupon);
    }

    @Transactional
    public CouponResponseDTO updateCoupon(Long couponId, CouponRequestDTO request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found: " + couponId));

        coupon.setDiscountType(com.krushna.commercecore.model.DiscountType.valueOf(request.getDiscountType()));
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setDiscountAmount(request.getDiscountAmount());
        coupon.setMinPurchaseAmount(request.getMinPurchaseAmount());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setActive(request.isActive());
        coupon.setUsageLimit(request.getUsageLimit());

        Coupon updated = couponRepository.save(coupon);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found: " + couponId));
        couponRepository.delete(coupon);
    }

    @Transactional
    public CouponResponseDTO useCoupon(String code) {
        Coupon coupon = couponRepository.findValidCouponByCode(code.toUpperCase(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired coupon"));

        if (!coupon.canBeUsed()) {
            throw new RuntimeException("Coupon usage limit exceeded");
        }

        coupon.incrementUsage();
        Coupon updated = couponRepository.save(coupon);
        return convertToDTO(updated);
    }

    public List<CouponResponseDTO> getExpiredCoupons() {
        List<Coupon> coupons = couponRepository.findExpiredOrInactiveCoupons(LocalDateTime.now());
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CouponResponseDTO convertToDTO(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountType().name(),
                coupon.getDiscountPercentage(),
                coupon.getDiscountAmount(),
                coupon.getMinPurchaseAmount(),
                coupon.getExpiryDate(),
                coupon.isActive(),
                coupon.getUsageLimit(),
                coupon.getTimesUsed(),
                coupon.getCreatedAt(),
                coupon.isValid()
        );
    }
}
