package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.ProductRequestDTO;
import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.dto.SellerAnalyticsDTO;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.service.ProductService;
import com.krushna.commercecore.service.SellerAnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin("*")
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SellerAnalyticsService sellerAnalyticsService;

    public SellerProductController(ProductService productService, 
                                    ProductRepository productRepository,
                                    UserRepository userRepository,
                                    SellerAnalyticsService sellerAnalyticsService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.sellerAnalyticsService = sellerAnalyticsService;
    }

    private User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        User currentUser = getCurrentLoggedInUser();
        dto.setSellerId(currentUser.getId());
        ProductResponseDTO created = productService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getSellerProducts() {
        User currentUser = getCurrentLoggedInUser();
        List<Product> products = productRepository.findBySeller(currentUser);
        List<ProductResponseDTO> dtos = products.stream()
                .map(product -> {
                    ProductResponseDTO dto = new ProductResponseDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setDescription(product.getDescription());
                    dto.setPrice(product.getPrice());
                    dto.setOriginalPrice(product.getOriginalPrice());
                    dto.setImageUrl(product.getImageUrl());
                    dto.setStockQuantity(product.getStockQuantity());
                    dto.setInStock(product.isInStock());
                    dto.setBrand(product.getBrand());
                    dto.setWarranty(product.getWarranty());
                    dto.setReturnPolicy(product.getReturnPolicy());
                    dto.setRating(product.getRating());
                    dto.setReviewCount(product.getReviewCount());
                    dto.setFeatures(product.getFeatures());
                    dto.setSpecs(product.getSpecs());
                    if (product.getSeller() != null) {
                        ProductResponseDTO.SellerInfo sellerInfo = new ProductResponseDTO.SellerInfo();
                        sellerInfo.setId(product.getSeller().getId());
                        sellerInfo.setUsername(product.getSeller().getUsername());
                        dto.setSeller(sellerInfo);
                    }
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getSellerProduct(@PathVariable Long id) {
        User currentUser = getCurrentLoggedInUser();
        Product product = productRepository.findByIdAndSeller(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Product not found or you don't have access to this product"));
        
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setInStock(product.isInStock());
        dto.setBrand(product.getBrand());
        dto.setWarranty(product.getWarranty());
        dto.setReturnPolicy(product.getReturnPolicy());
        dto.setRating(product.getRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setFeatures(product.getFeatures());
        dto.setSpecs(product.getSpecs());
        if (product.getSeller() != null) {
            ProductResponseDTO.SellerInfo sellerInfo = new ProductResponseDTO.SellerInfo();
            sellerInfo.setId(product.getSeller().getId());
            sellerInfo.setUsername(product.getSeller().getUsername());
            dto.setSeller(sellerInfo);
        }
        
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {
        User currentUser = getCurrentLoggedInUser();
        
        // Verify product belongs to current seller
        Product existing = productRepository.findByIdAndSeller(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Product not found or you don't have permission to update this product"));
        
        // Ensure sellerId cannot be changed to another seller
        dto.setSellerId(currentUser.getId());
        
        ProductResponseDTO updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        User currentUser = getCurrentLoggedInUser();
        
        // Verify product belongs to current seller
        Product existing = productRepository.findByIdAndSeller(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Product not found or you don't have permission to delete this product"));
        
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<SellerAnalyticsDTO> getSellerDashboard() {
        User currentUser = getCurrentLoggedInUser();
        SellerAnalyticsDTO analytics = sellerAnalyticsService.getSellerAnalytics(currentUser.getId());
        return ResponseEntity.ok(analytics);
    }
}
