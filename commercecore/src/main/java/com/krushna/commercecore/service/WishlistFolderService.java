package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.WishlistAnalyticsDTO;
import com.krushna.commercecore.dto.WishlistFolderDTO;
import com.krushna.commercecore.dto.WishlistItemDTO;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.model.WishlistAnalytics;
import com.krushna.commercecore.model.WishlistFolder;
import com.krushna.commercecore.model.WishlistItem;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.WishlistAnalyticsRepository;
import com.krushna.commercecore.repository.WishlistFolderRepository;
import com.krushna.commercecore.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistFolderService {

    private final WishlistFolderRepository folderRepository;
    private final WishlistItemRepository itemRepository;
    private final WishlistAnalyticsRepository analyticsRepository;
    private final ProductRepository productRepository;

    @Transactional
    public WishlistFolderDTO createFolder(Long userId, String name, String description) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = new WishlistFolder(user, name);
        folder.setDescription(description);

        return mapToDTO(folderRepository.save(folder));
    }

    @Transactional
    public void deleteFolder(Long userId, Long folderId) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = folderRepository.findByUserAndId(user, folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (folder.isDefault()) {
            throw new RuntimeException("Cannot delete default folder");
        }

        folderRepository.delete(folder);
    }

    @Transactional
    public WishlistFolderDTO updateFolder(Long userId, Long folderId, String name, String description, boolean isPublic) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = folderRepository.findByUserAndId(user, folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        folder.setName(name);
        folder.setDescription(description);
        folder.setPublic(isPublic);

        if (isPublic && folder.getShareToken() == null) {
            folder.setShareToken(UUID.randomUUID().toString());
        }

        return mapToDTO(folderRepository.save(folder));
    }

    @Transactional(readOnly = true)
    public List<WishlistFolderDTO> getUserFolders(Long userId) {
        User user = new User();
        user.setId(userId);

        return folderRepository.findByUserOrderByUpdatedAtDesc(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToFolder(Long userId, Long folderId, Long productId, boolean priceDropAlert, boolean stockAlert, double alertPrice) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = folderRepository.findByUserAndId(user, folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        itemRepository.findByFolderAndProductId(folder, productId)
                .ifPresentOrElse(
                        existing -> {},
                        () -> {
                            WishlistItem item = new WishlistItem(folder, product);
                            item.setPriceDropAlert(priceDropAlert);
                            item.setStockAlert(stockAlert);
                            item.setAlertPrice(alertPrice);
                            itemRepository.save(item);
                        }
                );

        // Track analytics
        trackAnalytics(user, product);
    }

    @Transactional
    public void removeFromFolder(Long userId, Long folderId, Long productId) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = folderRepository.findByUserAndId(user, folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        itemRepository.deleteByFolderAndProductId(folder, productId);
    }

    @Transactional
    public void bulkRemoveFromFolder(Long userId, Long folderId, List<Long> productIds) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = folderRepository.findByUserAndId(user, folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        productIds.forEach(productId -> 
            itemRepository.deleteByFolderAndProductId(folder, productId)
        );
    }

    @Transactional
    public void moveToCart(Long userId, Long folderId, Long productId) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = folderRepository.findByUserAndId(user, folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update analytics
        WishlistAnalytics analytics = analyticsRepository.findByUserAndProductId(user, productId)
                .orElse(new WishlistAnalytics(user, product));
        analytics.incrementMoveToCartCount();
        analyticsRepository.save(analytics);

        // Remove from wishlist
        itemRepository.deleteByFolderAndProductId(folder, productId);
    }

    @Transactional
    public void bulkMoveToCart(Long userId, Long folderId, List<Long> productIds) {
        productIds.forEach(productId -> moveToCart(userId, folderId, productId));
    }

    @Transactional(readOnly = true)
    public WishlistFolderDTO getFolder(Long userId, Long folderId) {
        User user = new User();
        user.setId(userId);

        WishlistFolder folder = folderRepository.findByUserAndId(user, folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        return mapToDTO(folder);
    }

    @Transactional(readOnly = true)
    public WishlistFolderDTO getSharedFolder(String shareToken) {
        WishlistFolder folder = folderRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new RuntimeException("Shared folder not found"));

        if (!folder.isPublic()) {
            throw new RuntimeException("Folder is not public");
        }

        return mapToDTO(folder);
    }

    @Transactional(readOnly = true)
    public List<WishlistAnalyticsDTO> getAnalytics(Long userId) {
        User user = new User();
        user.setId(userId);

        return analyticsRepository.findByUserOrderByViewCountDesc(user).stream()
                .map(this::mapToAnalyticsDTO)
                .collect(Collectors.toList());
    }

    private void trackAnalytics(User user, Product product) {
        WishlistAnalytics analytics = analyticsRepository.findByUserAndProductId(user, product.getId())
                .orElse(new WishlistAnalytics(user, product));
        analytics.incrementViewCount();
        analyticsRepository.save(analytics);
    }

    private WishlistFolderDTO mapToDTO(WishlistFolder folder) {
        WishlistFolderDTO dto = new WishlistFolderDTO();
        dto.setId(folder.getId());
        dto.setName(folder.getName());
        dto.setDescription(folder.getDescription());
        dto.setDefault(folder.isDefault());
        dto.setPublic(folder.isPublic());
        dto.setShareToken(folder.getShareToken());
        dto.setCreatedAt(folder.getCreatedAt());
        dto.setUpdatedAt(folder.getUpdatedAt());
        dto.setItemCount(folder.getItems().size());
        dto.setItems(folder.getItems().stream()
                .map(this::mapItemToDTO)
                .collect(Collectors.toSet()));
        return dto;
    }

    private WishlistItemDTO mapItemToDTO(WishlistItem item) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductImage(item.getProduct().getImageUrl());
        dto.setPrice(item.getProduct().getPrice());
        dto.setOriginalPrice(item.getProduct().getOriginalPrice());
        dto.setInStock(item.getProduct().isInStock());
        dto.setAddedAt(item.getAddedAt());
        dto.setPriceDropAlert(item.isPriceDropAlert());
        dto.setStockAlert(item.isStockAlert());
        dto.setAlertPrice(item.getAlertPrice());
        return dto;
    }

    private WishlistAnalyticsDTO mapToAnalyticsDTO(WishlistAnalytics analytics) {
        WishlistAnalyticsDTO dto = new WishlistAnalyticsDTO();
        dto.setProductId(analytics.getProduct().getId());
        dto.setProductName(analytics.getProduct().getName());
        dto.setViewCount(analytics.getViewCount());
        dto.setMoveToCartCount(analytics.getMoveToCartCount());
        dto.setLastViewedAt(analytics.getLastViewedAt());
        dto.setLastMovedToCartAt(analytics.getLastMovedToCartAt());
        return dto;
    }
}
