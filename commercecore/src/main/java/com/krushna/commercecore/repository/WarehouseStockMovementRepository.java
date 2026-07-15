package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.WarehouseStockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WarehouseStockMovementRepository extends JpaRepository<WarehouseStockMovement, Long> {
    List<WarehouseStockMovement> findByInventoryItemId(Long inventoryItemId);
}
