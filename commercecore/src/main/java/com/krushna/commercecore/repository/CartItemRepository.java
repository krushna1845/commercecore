package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser_Id(Long userId);

    Optional<CartItem> findByUser_IdAndProduct_Id(Long userId, Long productId);

    List<CartItem> findByGuestId(String guestId);

    Optional<CartItem> findByGuestIdAndProduct_Id(String guestId, Long productId);
}
