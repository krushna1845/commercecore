package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
    
    @Query("SELECT u FROM UserRecommendation u WHERE u.userId = ?1 AND u.recommendationType = ?2 AND (u.expiresAt IS NULL OR u.expiresAt > ?3) ORDER BY u.rankPosition ASC LIMIT ?4")
    List<UserRecommendation> findByUserIdAndType(Long userId, String type, LocalDateTime now, int limit);
    
    @Query("SELECT u FROM UserRecommendation u WHERE u.userId = ?1 AND u.recommendationType = ?2 ORDER BY u.score DESC LIMIT ?3")
    List<UserRecommendation> findTopByUserIdAndType(Long userId, String type, int limit);
    
    void deleteByUserIdAndRecommendationType(Long userId, String type);
    
    @Query("DELETE FROM UserRecommendation u WHERE u.expiresAt IS NOT NULL AND u.expiresAt <= ?1")
    void deleteExpired(LocalDateTime now);
}
