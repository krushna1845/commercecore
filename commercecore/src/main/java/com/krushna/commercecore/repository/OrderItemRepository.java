package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order o JOIN FETCH oi.product p JOIN FETCH p.seller WHERE p.seller.id = :sellerId ORDER BY o.createdAt DESC")
    List<OrderItem> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order o JOIN FETCH oi.product p JOIN FETCH p.seller WHERE oi.id = :itemId AND p.seller.id = :sellerId")
    Optional<OrderItem> findByIdAndSellerId(@Param("itemId") Long itemId, @Param("sellerId") Long sellerId);
}
