package com.krushna.commercecore.service;

import com.krushna.commercecore.model.*;
import com.krushna.commercecore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private WarehouseStockMovementRepository stockMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Transactional
    public InventoryItem addStock(Long warehouseId, Long productId, Long variantId, int quantity, String referenceId, String reason) {
        Optional<InventoryItem> itemOpt = (variantId == null) ?
                inventoryItemRepository.findByWarehouseIdAndProductIdAndVariantIdIsNull(warehouseId, productId) :
                inventoryItemRepository.findByWarehouseIdAndProductIdAndVariantId(warehouseId, productId, variantId);

        InventoryItem item = itemOpt.orElseThrow(() -> new RuntimeException("Inventory item not found"));
        item.setAvailableStock(item.getAvailableStock() + quantity);
        inventoryItemRepository.save(item);

        WarehouseStockMovement movement = new WarehouseStockMovement(item, WarehouseStockMovement.MovementType.INBOUND, quantity, referenceId, reason);
        stockMovementRepository.save(movement);
        
        return item;
    }

    @Transactional
    public boolean reserveStock(Long warehouseId, Long productId, Long variantId, int quantity, String referenceId) {
        Optional<InventoryItem> itemOpt = (variantId == null) ?
                inventoryItemRepository.findByWarehouseIdAndProductIdAndVariantIdIsNull(warehouseId, productId) :
                inventoryItemRepository.findByWarehouseIdAndProductIdAndVariantId(warehouseId, productId, variantId);

        if (itemOpt.isPresent()) {
            InventoryItem item = itemOpt.get();
            if (item.getAvailableStock() >= quantity) {
                item.setAvailableStock(item.getAvailableStock() - quantity);
                item.setReservedStock(item.getReservedStock() + quantity);
                inventoryItemRepository.save(item);

                WarehouseStockMovement movement = new WarehouseStockMovement(item, WarehouseStockMovement.MovementType.RESERVED, quantity, referenceId, "Order Placed");
                stockMovementRepository.save(movement);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void releaseStock(Long warehouseId, Long productId, Long variantId, int quantity, String referenceId) {
        Optional<InventoryItem> itemOpt = (variantId == null) ?
                inventoryItemRepository.findByWarehouseIdAndProductIdAndVariantIdIsNull(warehouseId, productId) :
                inventoryItemRepository.findByWarehouseIdAndProductIdAndVariantId(warehouseId, productId, variantId);

        if (itemOpt.isPresent()) {
            InventoryItem item = itemOpt.get();
            item.setReservedStock(item.getReservedStock() - quantity);
            item.setAvailableStock(item.getAvailableStock() + quantity);
            inventoryItemRepository.save(item);

            WarehouseStockMovement movement = new WarehouseStockMovement(item, WarehouseStockMovement.MovementType.RELEASED, quantity, referenceId, "Order Cancelled");
            stockMovementRepository.save(movement);
        }
    }

    public List<InventoryItem> getLowStockItems(int threshold) {
        return inventoryItemRepository.findByAvailableStockLessThanEqual(threshold);
    }
}
