package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.SearchSynonym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchSynonymRepository extends JpaRepository<SearchSynonym, Long> {
    Optional<SearchSynonym> findByKeywordIgnoreCase(String keyword);
}
