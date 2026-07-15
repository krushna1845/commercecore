package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.WishlistItem;
import com.krushna.commercecore.model.WishlistFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByFolderOrderByAddedAtDesc(WishlistFolder folder);
    Optional<WishlistItem> findByFolderAndProductId(WishlistFolder folder, Long productId);
    void deleteByFolder(WishlistFolder folder);
    void deleteByFolderAndProductId(WishlistFolder folder, Long productId);
    List<WishlistItem> findByFolderAndPriceDropAlertTrue(WishlistFolder folder);
    List<WishlistItem> findByFolderAndStockAlertTrue(WishlistFolder folder);
}
