package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.WishlistResponseDTO;
import com.krushna.commercecore.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<WishlistResponseDTO> addToWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        WishlistResponseDTO added = wishlistService.addToWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.ok(added);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponseDTO>> getUserWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<WishlistResponseDTO> wishlist = wishlistService.getUserWishlist(userDetails.getUsername());
        return ResponseEntity.ok(wishlist);
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> isInWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean inWishlist = wishlistService.isInWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.ok(inWishlist);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.clearWishlist(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
