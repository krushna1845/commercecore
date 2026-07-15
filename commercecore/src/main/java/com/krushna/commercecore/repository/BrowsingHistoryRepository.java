package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.BrowsingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BrowsingHistoryRepository extends JpaRepository<BrowsingHistory, Long> {
    
    List<BrowsingHistory> findByUserIdOrderByViewedAtDesc(Long userId);
    
    @Query("SELECT b FROM BrowsingHistory b WHERE b.userId = ?1 AND b.viewedAt >= ?2 ORDER BY b.viewedAt DESC LIMIT ?3")
    List<BrowsingHistory> findRecentByUserId(Long userId, LocalDateTime since, int limit);
    
    List<BrowsingHistory> findByProductIdOrderByViewedAtDesc(Long productId);
    
    @Query("SELECT COUNT(b) FROM BrowsingHistory b WHERE b.productId = ?1 AND b.viewedAt >= ?2")
    long countViewsSince(Long productId, LocalDateTime since);
}
