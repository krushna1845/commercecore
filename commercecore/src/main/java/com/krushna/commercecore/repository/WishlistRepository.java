package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Wishlist;
import com.krushna.commercecore.model.Wishlist.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT w FROM Wishlist w WHERE w.user.id = :userId ORDER BY w.addedAt DESC")
    List<Wishlist> findByUserIdOrderByAddedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}
