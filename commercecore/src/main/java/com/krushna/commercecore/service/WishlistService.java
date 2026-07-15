package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.WishlistResponseDTO;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.model.Wishlist;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                          UserRepository userRepository,
                          ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishlistResponseDTO addToWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Check if already in wishlist
        if (wishlistRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist(user, product);
        Wishlist saved = wishlistRepository.save(wishlist);

        return convertToDTO(saved);
    }

    @Transactional
    public void removeFromWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Product not found in wishlist"));

        wishlistRepository.delete(wishlist);
    }

    public List<WishlistResponseDTO> getUserWishlist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Wishlist> wishlistItems = wishlistRepository.findByUserIdOrderByAddedAtDesc(user.getId());
        return wishlistItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean isInWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return wishlistRepository.findByUserIdAndProductId(user.getId(), productId).isPresent();
    }

    @Transactional
    public void clearWishlist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(user.getId());
        wishlistRepository.deleteAll(wishlistItems);
    }

    private WishlistResponseDTO convertToDTO(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        return new WishlistResponseDTO(
                wishlist.getUser().getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.isInStock(),
                wishlist.getAddedAt()
        );
    }
}
