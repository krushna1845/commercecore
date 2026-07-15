package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.CouponRequestDTO;
import com.krushna.commercecore.dto.CouponResponseDTO;
import com.krushna.commercecore.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public ResponseEntity<CouponResponseDTO> validateCoupon(@RequestParam String code) {
        CouponResponseDTO coupon = couponService.validateCoupon(code);
        return ResponseEntity.ok(coupon);
    }

    @PostMapping("/use")
    public ResponseEntity<CouponResponseDTO> useCoupon(@RequestParam String code) {
        CouponResponseDTO coupon = couponService.useCoupon(code);
        return ResponseEntity.ok(coupon);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CouponResponseDTO>> getActiveCoupons() {
        List<CouponResponseDTO> coupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponseDTO> getCouponByCode(@PathVariable String code) {
        CouponResponseDTO coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(coupon);
    }

    // Admin-only endpoints
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponseDTO> createCoupon(@Valid @RequestBody CouponRequestDTO request) {
        CouponResponseDTO created = couponService.createCoupon(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        List<CouponResponseDTO> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @PutMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponseDTO> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody CouponRequestDTO request) {
        CouponResponseDTO updated = couponService.updateCoupon(couponId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CouponResponseDTO>> getExpiredCoupons() {
        List<CouponResponseDTO> coupons = couponService.getExpiredCoupons();
        return ResponseEntity.ok(coupons);
    }
}
