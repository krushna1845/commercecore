package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.SellerOrderItemDTO;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.service.SellerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/orders")
@CrossOrigin("*")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
public class SellerOrderController {

    private final SellerOrderService sellerOrderService;

    @GetMapping
    public ResponseEntity<List<SellerOrderItemDTO>> getSellerOrders() {
        User seller = getCurrentUser();
        return ResponseEntity.ok(sellerOrderService.getSellerOrderItems(seller.getId()));
    }

    @PutMapping("/items/{itemId}/status")
    public ResponseEntity<SellerOrderItemDTO> updateItemStatus(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        User seller = getCurrentUser();
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new RuntimeException("Status is required");
        }
        return ResponseEntity.ok(sellerOrderService.updateItemStatus(seller.getId(), itemId, status));
    }

    @PutMapping("/{itemId}/tracking")
    public ResponseEntity<SellerOrderItemDTO> updateTrackingInfo(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> payload) {
        User seller = getCurrentUser();
        String trackingNumber = payload.get("trackingNumber");
        String courierName = payload.get("courierName");
        return ResponseEntity.ok(sellerOrderService.updateTrackingInfo(seller.getId(), itemId, trackingNumber, courierName));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return sellerOrderService.getUserByUsername(auth.getName());
    }
}
