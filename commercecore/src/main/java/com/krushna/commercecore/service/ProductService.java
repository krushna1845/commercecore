package com.krushna.commercecore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.krushna.commercecore.dto.CategoryResponseDTO;
import com.krushna.commercecore.dto.ProductRequestDTO;
import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.exception.ProductNotFoundException;
import com.krushna.commercecore.model.ApprovalStatus;
import com.krushna.commercecore.model.Category;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.CategoryRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.UserRepository;

import jakarta.validation.Valid;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProductResponseDTO save(ProductRequestDTO dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));
        }

        User seller = null;
        if (dto.getSellerId() != null) {
            seller = userRepository.findById(dto.getSellerId())
                    .orElseThrow(() -> new RuntimeException("Seller not found with id: " + dto.getSellerId()));
        }

        Product product = new Product(
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getImageUrl(),
                dto.getStockQuantity(),
                category,
                seller
        );
        // Products created directly by an administrator have no seller review step.
        // Seller submissions remain pending until an administrator approves them.
        if (seller == null) {
            product.setApprovalStatus(ApprovalStatus.APPROVED);
        }
        product.setSku(dto.getSku());
        product.setWeight(dto.getWeight());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setBrand(dto.getBrand());
        product.setWarranty(dto.getWarranty());
        product.setReturnPolicy(dto.getReturnPolicy());
        product.setRating(dto.getRating());
        product.setReviewCount(dto.getReviewCount());
        product.setFeatures(dto.getFeatures() != null ? dto.getFeatures() : new ArrayList<>());
        product.setSpecs(dto.getSpecs() != null ? dto.getSpecs() : new HashMap<>());

        Product saved = repository.save(product);
        return convertToDTO(saved);
    }


    public List<ProductResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getApprovedProducts() {
        List<Product> approvedProducts = repository.findByApprovalStatus(ApprovalStatus.APPROVED);
        // Existing catalog entries created before seller moderation were stored as
        // PENDING with no seller. Keep those legacy admin products visible without
        // exposing actual seller submissions awaiting review.
        if (approvedProducts.isEmpty()) {
            approvedProducts = repository.findBySellerIsNullAndApprovalStatus(ApprovalStatus.PENDING);
        }
        return approvedProducts
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getPendingProducts() {
        return repository.findByApprovalStatus(ApprovalStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDTO update(Long id, @Valid ProductRequestDTO product) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = null;
        if (product.getCategoryId() != null) {
            category = categoryRepository.findById(product.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + product.getCategoryId()));
        }

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setSku(product.getSku());
        existing.setWeight(product.getWeight());
        existing.setPrice(product.getPrice());
        existing.setOriginalPrice(product.getOriginalPrice());
        existing.setImageUrl(product.getImageUrl());
        existing.setStockQuantity(product.getStockQuantity());
        existing.setCategory(category);
        existing.setBrand(product.getBrand());
        existing.setWarranty(product.getWarranty());
        existing.setReturnPolicy(product.getReturnPolicy());
        existing.setRating(product.getRating());
        existing.setReviewCount(product.getReviewCount());
        existing.setFeatures(product.getFeatures() != null ? product.getFeatures() : new ArrayList<>());
        existing.setSpecs(product.getSpecs() != null ? product.getSpecs() : new HashMap<>());

        if (product.getSellerId() != null) {
            User seller = userRepository.findById(product.getSellerId())
                    .orElseThrow(() -> new RuntimeException("Seller not found with id: " + product.getSellerId()));
            existing.setSeller(seller);
        }

        Product updated = repository.save(existing);
        return convertToDTO(updated);
    }
    public void delete(Long id) {
        repository.deleteById(id);
    }
    public Page<ProductResponseDTO> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable)
                .map(this::convertToDTO);
    }


    public ProductResponseDTO getById(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));


        return convertToDTO(product);
    }




    public List<ProductResponseDTO> searchByName(String name) {

        List<Product> products = repository.findByNameContainingIgnoreCase(name);

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }




    public ProductResponseDTO convertToDTO(Product product) {
        CategoryResponseDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = new CategoryResponseDTO(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getDescription()
            );
        }

        ProductResponseDTO.SellerInfo sellerInfo = null;
        if (product.getSeller() != null) {
            sellerInfo = new ProductResponseDTO.SellerInfo(
                    product.getSeller().getId(),
                    product.getSeller().getUsername()
            );
        }

        ProductResponseDTO dto = new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.isInStock(),
                product.getBrand(),
                product.getWarranty(),
                product.getReturnPolicy(),
                product.getRating(),
                product.getReviewCount(),
                product.getFeatures(),
                product.getSpecs(),
                categoryDTO,
                sellerInfo,
                product.getApprovalStatus() != null ? product.getApprovalStatus().name() : "PENDING"
        );
        dto.setSku(product.getSku());
        dto.setWeight(product.getWeight());
        return dto;
    }


    public Page<ProductResponseDTO> getProducts(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return repository.findByApprovalStatus(ApprovalStatus.APPROVED, pageable)
                .map(this::convertToDTO);
    }

    public List<ProductResponseDTO> compareProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(id -> repository.findById(id).map(this::convertToDTO).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
