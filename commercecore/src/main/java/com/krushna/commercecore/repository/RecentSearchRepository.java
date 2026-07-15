package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {
    Optional<RecentSearch> findByQueryIgnoreCaseAndUserId(String query, Long userId);
    Optional<RecentSearch> findByQueryIgnoreCaseAndUserIdIsNull(String query);
    List<RecentSearch> findTop10ByOrderBySearchCountDesc();
}
