package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.GiftWrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftWrapRepository extends JpaRepository<GiftWrap, Long> {
    List<GiftWrap> findByActiveTrueOrderByPriceAsc();
}
