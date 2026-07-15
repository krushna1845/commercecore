package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.CartItemRequestDTO;
import com.krushna.commercecore.dto.CartResponseDTO;
import com.krushna.commercecore.model.CartItem;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.CartItemRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User sampleUser;
    private Product sampleProduct;
    private CartItem sampleCartItem;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUsername("testuser");
        // Reflectively set the id since there's no setter in User
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(sampleUser, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sampleProduct = new Product("Headphones", 49.99);
        sampleProduct.setId(10L);
        
        sampleCartItem = new CartItem(sampleUser, null, sampleProduct, 1);
        // Reflectively set the id for CartItem
        try {
            var field = CartItem.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(sampleCartItem, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ─────────────────────── addToCart() ───────────────────────────────────

    @Test
    @DisplayName("addToCart() - should save new cart item for first-time add")
    void addToCart_shouldSaveNewCartItem() {
        CartItemRequestDTO request = new CartItemRequestDTO(10L, 2);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(sampleProduct));
        when(cartItemRepository.findByUser_IdAndProduct_Id(1L, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.findByUser_Id(1L)).thenReturn(List.of(
                new CartItem(sampleUser, null, sampleProduct, 2)
        ));

        CartResponseDTO result = cartService.addToCart("testuser", null, request);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("addToCart() - should increment quantity when product already in cart")
    void addToCart_shouldIncrementQuantity_whenItemAlreadyInCart() {
        CartItemRequestDTO request = new CartItemRequestDTO(10L, 3);
        CartItem existingItem = new CartItem(sampleUser, null, sampleProduct, 2);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(sampleProduct));
        when(cartItemRepository.findByUser_IdAndProduct_Id(1L, 10L))
                .thenReturn(Optional.of(existingItem));
        when(cartItemRepository.findByUser_Id(1L)).thenReturn(List.of(existingItem));

        cartService.addToCart("testuser", null, request);

        // Quantity 2 + 3 = 5
        verify(cartItemRepository, times(1)).save(argThat(item -> item.getQuantity() == 5));
    }

    // ─────────────────────── removeFromCart() ──────────────────────────────

    @Test
    @DisplayName("removeFromCart() - should find and delete cart item")
    void removeFromCart_shouldDeleteCartItem() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(cartItemRepository.findByUser_IdAndProduct_Id(1L, 10L)).thenReturn(Optional.of(sampleCartItem));
        doNothing().when(cartItemRepository).delete(sampleCartItem);
        when(cartItemRepository.findByUser_Id(1L)).thenReturn(List.of());

        CartResponseDTO result = cartService.removeFromCart("testuser", null, 10L);

        verify(cartItemRepository, times(1)).delete(sampleCartItem);
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ─────────────────────── getCart() ─────────────────────────────────────

    @Test
    @DisplayName("getCart() - should calculate total price correctly using BigDecimal")
    void getCart_shouldCalculateTotalPriceCorrectly() {
        Product product2 = new Product("Keyboard", 79.99);
        product2.setId(20L);

        CartItem item1 = new CartItem(sampleUser, null, sampleProduct, 2); // 2 × 49.99 = 99.98
        CartItem item2 = new CartItem(sampleUser, null, product2, 1);       // 1 × 79.99 = 79.99

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(cartItemRepository.findByUser_Id(1L)).thenReturn(List.of(item1, item2));

        CartResponseDTO result = cartService.getCart("testuser", null);

        assertThat(result.getItems()).hasSize(2);
        // Expected total: 99.98 + 79.99 = 179.97
        assertThat(result.getTotalPrice())
                .isEqualByComparingTo(new BigDecimal("179.97"));
    }

    @Test
    @DisplayName("getCart() - should return zero total for empty cart")
    void getCart_shouldReturnZeroTotal_whenCartIsEmpty() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(cartItemRepository.findByUser_Id(1L)).thenReturn(List.of());

        CartResponseDTO result = cartService.getCart("testuser", null);

        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("addToCart() - should throw RuntimeException when user not found")
    void addToCart_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                cartService.addToCart("ghost", null, new CartItemRequestDTO(10L, 1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
