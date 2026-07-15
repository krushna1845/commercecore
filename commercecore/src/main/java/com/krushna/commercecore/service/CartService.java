package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.*;
import com.krushna.commercecore.exception.ProductNotFoundException;
import com.krushna.commercecore.model.CartItem;
import com.krushna.commercecore.model.Coupon;
import com.krushna.commercecore.model.DiscountType;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.CartItemRepository;
import com.krushna.commercecore.repository.CouponRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.OptimisticLockException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       CouponRepository couponRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CartResponseDTO addToCart(String username, String guestId, CartItemRequestDTO request) {
        try {
            User user = username != null ? findUser(username) : null;
            Product product = findProduct(request.getProductId());

            Optional<CartItem> existing;
            if (user != null) {
                existing = cartItemRepository.findByUser_IdAndProduct_Id(user.getId(), product.getId());
            } else {
                existing = cartItemRepository.findByGuestIdAndProduct_Id(guestId, product.getId());
            }

            if (existing.isPresent()) {
                CartItem item = existing.get();
                item.setQuantity(item.getQuantity() + request.getQuantity());
                cartItemRepository.save(item);
            } else {
                CartItem newItem = new CartItem(user, guestId, product, request.getQuantity());
                cartItemRepository.save(newItem);
            }

            return getCart(username, guestId);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Cart was modified by another process. Please try again.", e);
        }
    }

    @Transactional
    public CartResponseDTO removeFromCart(String username, String guestId, Long productId) {
        try {
            User user = username != null ? findUser(username) : null;
            Optional<CartItem> item;
            if (user != null) {
                item = cartItemRepository.findByUser_IdAndProduct_Id(user.getId(), productId);
            } else {
                item = cartItemRepository.findByGuestIdAndProduct_Id(guestId, productId);
            }
            item.ifPresent(cartItemRepository::delete);
            return getCart(username, guestId);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Cart was modified by another process. Please try again.", e);
        }
    }

    @Transactional
    public CartResponseDTO updateCartItemQuantity(String username, String guestId, CartItemRequestDTO request) {
        try {
            User user = username != null ? findUser(username) : null;
            Product product = findProduct(request.getProductId());

            Optional<CartItem> existing;
            if (user != null) {
                existing = cartItemRepository.findByUser_IdAndProduct_Id(user.getId(), product.getId());
            } else {
                existing = cartItemRepository.findByGuestIdAndProduct_Id(guestId, product.getId());
            }

            if (existing.isPresent()) {
                CartItem item = existing.get();
                item.setQuantity(request.getQuantity());
                cartItemRepository.save(item);
            } else {
                CartItem newItem = new CartItem(user, guestId, product, request.getQuantity());
                cartItemRepository.save(newItem);
            }

            return getCart(username, guestId);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Cart was modified by another process. Please try again.", e);
        }
    }

    public CartResponseDTO getCart(String username, String guestId) {
        List<CartItem> items = fetchItems(username, guestId);

        List<CartItemResponseDTO> itemDTOs = items.stream()
                .map(this::toItemResponseDTO)
                .toList();

        BigDecimal totalPrice = itemDTOs.stream()
                .map(CartItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new CartResponseDTO(itemDTOs, totalPrice);
    }

    @Transactional
    public void mergeGuestCart(String guestId, String username) {
        if (guestId == null || username == null) return;
        User user = findUser(username);
        List<CartItem> guestItems = cartItemRepository.findByGuestId(guestId);
        if (guestItems.isEmpty()) return;

        List<CartItem> userItems = cartItemRepository.findByUser_Id(user.getId());

        for (CartItem gItem : guestItems) {
            Optional<CartItem> matchingUserItem = userItems.stream()
                .filter(uItem -> uItem.getProduct().getId().equals(gItem.getProduct().getId()))
                .findFirst();
            
            if (matchingUserItem.isPresent()) {
                CartItem uItem = matchingUserItem.get();
                uItem.setQuantity(uItem.getQuantity() + gItem.getQuantity());
                cartItemRepository.save(uItem);
                cartItemRepository.delete(gItem);
            } else {
                gItem.setUser(user);
                gItem.setGuestId(null);
                cartItemRepository.save(gItem);
            }
        }
    }

    public CartPreviewDTO getCartCheckoutPreview(String username, String guestId, String couponCode) {
        List<CartItem> items = fetchItems(username, guestId);
        
        // Group by Seller ID. If seller is null, group as 0 (Admin/System)
        Map<Long, List<CartItem>> grouped = items.stream()
            .collect(Collectors.groupingBy(item -> item.getProduct().getSeller() != null ? item.getProduct().getSeller().getId() : 0L));

        List<SellerOrderPreviewDTO> sellerOrders = new ArrayList<>();
        BigDecimal grandSubtotal = BigDecimal.ZERO;
        BigDecimal grandShipping = BigDecimal.ZERO;
        BigDecimal grandTax = BigDecimal.ZERO;

        for (Map.Entry<Long, List<CartItem>> entry : grouped.entrySet()) {
            Long sellerId = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();
            
            String sellerName = sellerId == 0L ? "Admin" : userRepository.findById(sellerId).map(User::getUsername).orElse("Unknown");
            List<CartItemResponseDTO> itemDTOs = sellerItems.stream().map(this::toItemResponseDTO).toList();
            
            BigDecimal sellerSubtotal = itemDTOs.stream().map(CartItemResponseDTO::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Dummy logic: $5 flat rate shipping per seller, + 8% tax.
            BigDecimal sellerShipping = new BigDecimal("5.00");
            BigDecimal sellerTax = sellerSubtotal.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
            
            BigDecimal sellerTotal = sellerSubtotal.add(sellerShipping).add(sellerTax);
            
            sellerOrders.add(new SellerOrderPreviewDTO(sellerId, sellerName, itemDTOs, sellerSubtotal, sellerShipping, sellerTax, sellerTotal));
            
            grandSubtotal = grandSubtotal.add(sellerSubtotal);
            grandShipping = grandShipping.add(sellerShipping);
            grandTax = grandTax.add(sellerTax);
        }

        BigDecimal discountTotal = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Optional<Coupon> opt = couponRepository.findValidCouponByCode(couponCode.toUpperCase(), LocalDateTime.now());
            if (opt.isPresent()) {
                Coupon coupon = opt.get();
                if (grandSubtotal.compareTo(BigDecimal.valueOf(coupon.getMinPurchaseAmount())) >= 0) {
                    if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                        discountTotal = grandSubtotal.multiply(BigDecimal.valueOf(coupon.getDiscountPercentage())).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                        discountTotal = BigDecimal.valueOf(coupon.getDiscountAmount());
                    } else if (coupon.getDiscountType() == DiscountType.FREE_SHIPPING) {
                        discountTotal = grandShipping;
                    }
                }
            }
        }

        BigDecimal grandTotal = grandSubtotal.add(grandShipping).add(grandTax).subtract(discountTotal);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        return new CartPreviewDTO(sellerOrders, grandSubtotal, grandShipping, grandTax, discountTotal, grandTotal);
    }

    public List<CartItem> getRawCartItems(String username) {
        User user = findUser(username);
        return cartItemRepository.findByUser_Id(user.getId());
    }

    @Transactional
    public void clearCart(String username) {
        try {
            User user = findUser(username);
            List<CartItem> items = cartItemRepository.findByUser_Id(user.getId());
            cartItemRepository.deleteAll(items);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("Cart was modified by another process. Please try again.", e);
        }
    }

    private List<CartItem> fetchItems(String username, String guestId) {
        if (username != null) {
            return cartItemRepository.findByUser_Id(findUser(username).getId());
        } else if (guestId != null) {
            return cartItemRepository.findByGuestId(guestId);
        }
        return new ArrayList<>();
    }

    private CartItemResponseDTO toItemResponseDTO(CartItem item) {
        BigDecimal price = BigDecimal.valueOf(item.getProduct().getPrice())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponseDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                price,
                item.getQuantity(),
                subtotal
        );
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
    }
}
