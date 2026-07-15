package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.WishlistFolder;
import com.krushna.commercecore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistFolderRepository extends JpaRepository<WishlistFolder, Long> {
    List<WishlistFolder> findByUserOrderByUpdatedAtDesc(User user);
    Optional<WishlistFolder> findByUserAndId(User user, Long id);
    Optional<WishlistFolder> findByShareToken(String shareToken);
    List<WishlistFolder> findByUserAndIsPublicTrue(User user);
    void deleteByUser(User user);
}
