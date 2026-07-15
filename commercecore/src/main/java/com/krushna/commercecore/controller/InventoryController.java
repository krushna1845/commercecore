package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.StockReservationRequestDTO;
import com.krushna.commercecore.model.InventoryItem;
import com.krushna.commercecore.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<InventoryItem> addStock(@RequestParam Long warehouseId, 
                                                  @RequestParam Long productId, 
                                                  @RequestParam(required = false) Long variantId, 
                                                  @RequestParam int quantity, 
                                                  @RequestParam String referenceId, 
                                                  @RequestParam String reason) {
        InventoryItem item = inventoryService.addStock(warehouseId, productId, variantId, quantity, referenceId, reason);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveStock(@RequestParam Long warehouseId, @RequestBody StockReservationRequestDTO request) {
        boolean success = inventoryService.reserveStock(warehouseId, request.getProductId(), request.getVariantId(), request.getQuantity(), request.getReferenceId());
        if (success) {
            return ResponseEntity.ok("Stock reserved successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to reserve stock: Insufficient availability");
        }
    }

    @PostMapping("/release")
    public ResponseEntity<String> releaseStock(@RequestParam Long warehouseId, @RequestBody StockReservationRequestDTO request) {
        inventoryService.releaseStock(warehouseId, request.getProductId(), request.getVariantId(), request.getQuantity(), request.getReferenceId());
        return ResponseEntity.ok("Stock released successfully");
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItem>> getLowStockItems(@RequestParam(defaultValue = "10") int threshold) {
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems(threshold);
        return ResponseEntity.ok(lowStockItems);
    }
}
