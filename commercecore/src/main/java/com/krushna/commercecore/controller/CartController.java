package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.CartItemRequestDTO;
import com.krushna.commercecore.dto.CartPreviewDTO;
import com.krushna.commercecore.dto.CartResponseDTO;
import com.krushna.commercecore.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin("*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String guestId) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        CartResponseDTO cart = cartService.getCart(username, guestId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String guestId,
            @Valid @RequestBody CartItemRequestDTO request) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        CartResponseDTO cart = cartService.addToCart(username, guestId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String guestId,
            @PathVariable Long productId) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        CartResponseDTO cart = cartService.removeFromCart(username, guestId, productId);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponseDTO> updateCartItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String guestId,
            @Valid @RequestBody CartItemRequestDTO request) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        CartResponseDTO cart = cartService.updateCartItemQuantity(username, guestId, request);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> mergeGuestCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        if (userDetails != null) {
            String guestId = body.get("guestId");
            cartService.mergeGuestCart(guestId, userDetails.getUsername());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/preview")
    public ResponseEntity<CartPreviewDTO> getCartCheckoutPreview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String guestId,
            @RequestBody(required = false) Map<String, String> body) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        String couponCode = (body != null) ? body.get("couponCode") : null;
        CartPreviewDTO preview = cartService.getCartCheckoutPreview(username, guestId, couponCode);
        return ResponseEntity.ok(preview);
    }
}
