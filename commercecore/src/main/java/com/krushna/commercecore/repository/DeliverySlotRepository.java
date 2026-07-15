package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.DeliverySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliverySlotRepository extends JpaRepository<DeliverySlot, Long> {
    List<DeliverySlot> findBySlotDateAndAvailableTrueOrderByStartTime(String slotDate);
    List<DeliverySlot> findBySlotDateGreaterThanEqualAndAvailableTrueOrderBySlotDateAscStartTimeAsc(String slotDate);
}
