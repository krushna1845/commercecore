package com.krushna.commercecore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.krushna.commercecore.model.ApprovalStatus;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Product> {

    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByCategoryIdAndStockQuantityGreaterThan(Long categoryId, int minStock);
    
    List<Product> findByStockQuantityGreaterThan(int minStock);

    List<Product> findBySeller(User seller);

    Optional<Product> findByIdAndSeller(Long id, User seller);

    List<Product> findBySellerAndStockQuantityGreaterThan(User seller, int minStock);

    List<Product> findByApprovalStatus(ApprovalStatus approvalStatus);

    /** Legacy/admin products have no seller and may predate the approval workflow. */
    List<Product> findBySellerIsNullAndApprovalStatus(ApprovalStatus approvalStatus);

    Page<Product> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
}
