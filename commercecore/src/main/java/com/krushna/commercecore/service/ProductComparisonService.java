package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.ComparisonResultDTO;
import com.krushna.commercecore.dto.ProductComparisonDTO;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.ProductComparison;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.ProductComparisonRepository;
import com.krushna.commercecore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductComparisonService {

    private final ProductComparisonRepository comparisonRepository;
    private final ProductRepository productRepository;
    private static final int MAX_COMPARISON_ITEMS = 4;

    @Transactional
    public void addToComparison(Long userId, Long productId) {
        User user = new User();
        user.setId(userId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (comparisonRepository.countByUser(user) >= MAX_COMPARISON_ITEMS) {
            throw new RuntimeException("Maximum comparison limit reached. You can compare up to 4 products.");
        }

        comparisonRepository.findByUserAndProductId(user, productId)
                .ifPresentOrElse(
                        existing -> {},
                        () -> comparisonRepository.save(new ProductComparison(user, product))
                );
    }

    @Transactional
    public void removeFromComparison(Long userId, Long productId) {
        User user = new User();
        user.setId(userId);
        comparisonRepository.deleteByUserAndProductId(user, productId);
    }

    @Transactional
    public void clearComparison(Long userId) {
        User user = new User();
        user.setId(userId);
        comparisonRepository.deleteByUser(user);
    }

    @Transactional(readOnly = true)
    public ComparisonResultDTO getComparison(Long userId) {
        User user = new User();
        user.setId(userId);
        
        List<ProductComparison> comparisons = comparisonRepository.findByUserOrderByAddedAtDesc(user);
        List<ProductComparisonDTO> productDTOs = comparisons.stream()
                .map(comparison -> mapToDTO(comparison.getProduct()))
                .collect(Collectors.toList());

        // Highlight best values
        highlightBestValues(productDTOs);

        return new ComparisonResultDTO(
                productDTOs,
                productDTOs.size(),
                productDTOs.size() < MAX_COMPARISON_ITEMS
        );
    }

    private ProductComparisonDTO mapToDTO(Product product) {
        ProductComparisonDTO dto = new ProductComparisonDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setImageUrl(product.getImageUrl());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setDiscountPercentage(calculateDiscount(product.getPrice(), product.getOriginalPrice()));
        dto.setRating(product.getRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setBrand(product.getBrand());
        dto.setWarranty(product.getWarranty());
        dto.setReturnPolicy(product.getReturnPolicy());
        dto.setSpecs(product.getSpecs());
        dto.setFeatures(product.getFeatures());
        dto.setInStock(product.isInStock());
        return dto;
    }

    private double calculateDiscount(double price, double originalPrice) {
        if (originalPrice > 0 && originalPrice > price) {
            return ((originalPrice - price) / originalPrice) * 100;
        }
        return 0;
    }

    private void highlightBestValues(List<ProductComparisonDTO> products) {
        if (products.isEmpty()) return;

        // Find best price (lowest)
        ProductComparisonDTO bestPrice = products.stream()
                .min(Comparator.comparing(ProductComparisonDTO::getPrice))
                .orElse(null);
        if (bestPrice != null) bestPrice.setBestPrice(true);

        // Find best rating (highest)
        ProductComparisonDTO bestRating = products.stream()
                .max(Comparator.comparing(ProductComparisonDTO::getRating))
                .orElse(null);
        if (bestRating != null) bestRating.setBestRating(true);

        // Find best discount (highest)
        ProductComparisonDTO bestDiscount = products.stream()
                .max(Comparator.comparing(ProductComparisonDTO::getDiscountPercentage))
                .orElse(null);
        if (bestDiscount != null) bestDiscount.setBestDiscount(true);
    }
}
