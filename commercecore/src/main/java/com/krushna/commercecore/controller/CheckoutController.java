package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.CheckoutRequestDTO;
import com.krushna.commercecore.dto.CheckoutResponseDTO;
import com.krushna.commercecore.dto.OrderSummaryDTO;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Checkout management APIs")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserRepository userRepository;

    @PostMapping("/initialize")
    @Operation(summary = "Initialize checkout with available options")
    public ResponseEntity<CheckoutResponseDTO> initializeCheckout(
            @RequestBody CheckoutRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        CheckoutResponseDTO response = checkoutService.initializeCheckout(user.getId(), request.getItems());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate order summary with selected options")
    public ResponseEntity<OrderSummaryDTO> calculateOrderSummary(
            @RequestBody CheckoutRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderSummaryDTO summary = checkoutService.calculateOrderSummary(
                request.getItems(),
                request.getShippingMethodId(),
                request.getDeliverySlotId(),
                request.getGiftWrapId(),
                request.getCouponCode(),
                request.isGstInvoice(),
                18 // GST rate for India
        );
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/place-order")
    @Operation(summary = "Place order with all checkout details")
    public ResponseEntity<com.krushna.commercecore.dto.CheckoutSuccessDTO> placeOrder(
            @RequestBody CheckoutRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        com.krushna.commercecore.dto.CheckoutSuccessDTO response = checkoutService.placeOrder(user.getId(), request);
        return ResponseEntity.ok(response);
    }
}
