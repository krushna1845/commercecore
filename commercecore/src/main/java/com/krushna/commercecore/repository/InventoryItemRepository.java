package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByProductId(Long productId);
    Optional<InventoryItem> findByWarehouseIdAndProductIdAndVariantId(Long warehouseId, Long productId, Long variantId);
    Optional<InventoryItem> findByWarehouseIdAndProductIdAndVariantIdIsNull(Long warehouseId, Long productId);
    List<InventoryItem> findByAvailableStockLessThanEqual(int threshold);
}
