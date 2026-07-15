package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.PayoutDTO;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.service.PayoutService;
import com.krushna.commercecore.service.SellerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;
    private final SellerOrderService sellerOrderService; // to extract user from Auth

    @PostMapping("/request")
    public ResponseEntity<PayoutDTO> requestPayout(@RequestBody Map<String, Double> payload) {
        User seller = getCurrentUser();
        Double amount = payload.get("amount");
        return ResponseEntity.ok(payoutService.requestPayout(seller.getId(), amount));
    }

    @GetMapping
    public ResponseEntity<List<PayoutDTO>> getMyPayouts() {
        User seller = getCurrentUser();
        return ResponseEntity.ok(payoutService.getSellerPayouts(seller.getId()));
    }

    // Admins only (could map to /api/admin/payouts in a real production environment)
    @PostMapping("/{payoutId}/process")
    public ResponseEntity<PayoutDTO> processPayout(
            @PathVariable Long payoutId,
            @RequestBody Map<String, Object> payload) {
        String referenceNumber = (String) payload.get("referenceNumber");
        Boolean isSuccessful = (Boolean) payload.get("isSuccessful");
        return ResponseEntity.ok(payoutService.processPayout(payoutId, referenceNumber, isSuccessful));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return sellerOrderService.getUserByUsername(auth.getName());
    }
}
