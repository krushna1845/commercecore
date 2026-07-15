package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.WishlistAnalyticsDTO;
import com.krushna.commercecore.dto.WishlistFolderDTO;
import com.krushna.commercecore.service.WishlistFolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist/folders")
@RequiredArgsConstructor
@Tag(name = "Wishlist Folders", description = "Wishlist folder management APIs")
public class WishlistFolderController {

    private final WishlistFolderService folderService;

    @PostMapping
    @Operation(summary = "Create a new wishlist folder")
    public ResponseEntity<WishlistFolderDTO> createFolder(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(folderService.createFolder(userId, name, description));
    }

    @PutMapping("/{folderId}")
    @Operation(summary = "Update wishlist folder")
    public ResponseEntity<WishlistFolderDTO> updateFolder(
            @PathVariable Long folderId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false, defaultValue = "false") boolean isPublic,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(folderService.updateFolder(userId, folderId, name, description, isPublic));
    }

    @DeleteMapping("/{folderId}")
    @Operation(summary = "Delete wishlist folder")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable Long folderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        folderService.deleteFolder(userId, folderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all user folders")
    public ResponseEntity<List<WishlistFolderDTO>> getUserFolders(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(folderService.getUserFolders(userId));
    }

    @GetMapping("/{folderId}")
    @Operation(summary = "Get specific folder")
    public ResponseEntity<WishlistFolderDTO> getFolder(
            @PathVariable Long folderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(folderService.getFolder(userId, folderId));
    }

    @GetMapping("/shared/{shareToken}")
    @Operation(summary = "Get shared folder by token")
    public ResponseEntity<WishlistFolderDTO> getSharedFolder(@PathVariable String shareToken) {
        return ResponseEntity.ok(folderService.getSharedFolder(shareToken));
    }

    @PostMapping("/{folderId}/items")
    @Operation(summary = "Add product to folder")
    public ResponseEntity<Void> addToFolder(
            @PathVariable Long folderId,
            @RequestParam Long productId,
            @RequestParam(required = false, defaultValue = "true") boolean priceDropAlert,
            @RequestParam(required = false, defaultValue = "true") boolean stockAlert,
            @RequestParam(required = false, defaultValue = "0") double alertPrice,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        folderService.addToFolder(userId, folderId, productId, priceDropAlert, stockAlert, alertPrice);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{folderId}/items/{productId}")
    @Operation(summary = "Remove product from folder")
    public ResponseEntity<Void> removeFromFolder(
            @PathVariable Long folderId,
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        folderService.removeFromFolder(userId, folderId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{folderId}/items/bulk")
    @Operation(summary = "Bulk remove products from folder")
    public ResponseEntity<Void> bulkRemoveFromFolder(
            @PathVariable Long folderId,
            @RequestBody List<Long> productIds,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        folderService.bulkRemoveFromFolder(userId, folderId, productIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{folderId}/items/{productId}/move-to-cart")
    @Operation(summary = "Move product to cart")
    public ResponseEntity<Void> moveToCart(
            @PathVariable Long folderId,
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        folderService.moveToCart(userId, folderId, productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{folderId}/items/bulk/move-to-cart")
    @Operation(summary = "Bulk move products to cart")
    public ResponseEntity<Void> bulkMoveToCart(
            @PathVariable Long folderId,
            @RequestBody List<Long> productIds,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        folderService.bulkMoveToCart(userId, folderId, productIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get wishlist analytics")
    public ResponseEntity<List<WishlistAnalyticsDTO>> getAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(folderService.getAnalytics(userId));
    }
}
