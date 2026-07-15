package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.ProductRequestDTO;
import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.exception.ProductNotFoundException;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("Laptop", 999.99);
        sampleProduct.setId(1L);
    }

    // ─────────────────────── save() ────────────────────────────────────────

    @Test
    @DisplayName("save() - should create product and return DTO")
    void save_shouldReturnProductResponseDTO() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Laptop");
        dto.setPrice(999.99);

        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        ProductResponseDTO result = productService.save(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getPrice()).isEqualTo(999.99);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ─────────────────────── getAll() ──────────────────────────────────────

    @Test
    @DisplayName("getAll() - should return list of all product DTOs")
    void getAll_shouldReturnAllProducts() {
        Product product2 = new Product("Mouse", 29.99);
        product2.setId(2L);

        when(productRepository.findAll()).thenReturn(List.of(sampleProduct, product2));

        List<ProductResponseDTO> result = productService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(1).getName()).isEqualTo("Mouse");
    }

    @Test
    @DisplayName("getAll() - should return empty list when no products exist")
    void getAll_shouldReturnEmptyList_whenNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponseDTO> result = productService.getAll();

        assertThat(result).isEmpty();
    }

    // ─────────────────────── getById() ─────────────────────────────────────

    @Test
    @DisplayName("getById() - should return correct DTO for existing product")
    void getById_shouldReturnProductDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductResponseDTO result = productService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getPrice()).isEqualTo(999.99);
    }

    @Test
    @DisplayName("getById() - should throw ProductNotFoundException for unknown id")
    void getById_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─────────────────────── update() ──────────────────────────────────────

    @Test
    @DisplayName("update() - should modify and save the existing product")
    void update_shouldModifyAndSaveProduct() {
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Gaming Laptop");
        updateDTO.setPrice(1499.99);

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        ProductResponseDTO updated = productService.update(1L, updateDTO);

        assertThat(updated.getName()).isEqualTo("Gaming Laptop");
        assertThat(updated.getPrice()).isEqualTo(1499.99);
        verify(productRepository, times(1)).save(argThat(p ->
                "Gaming Laptop".equals(p.getName()) && p.getPrice() == 1499.99
        ));
    }

    @Test
    @DisplayName("update() - should throw when product not found")
    void update_shouldThrow_whenProductNotFound() {
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Ghost Product");
        updateDTO.setPrice(1.0);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }

    // ─────────────────────── delete() ──────────────────────────────────────

    @Test
    @DisplayName("delete() - should call repository deleteById")
    void delete_shouldCallRepositoryDeleteById() {
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    // ─────────────────────── searchByName() ────────────────────────────────

    @Test
    @DisplayName("searchByName() - should return matching products")
    void searchByName_shouldReturnMatchingProducts() {
        when(productRepository.findByNameContainingIgnoreCase("lap"))
                .thenReturn(List.of(sampleProduct));

        List<ProductResponseDTO> result = productService.searchByName("lap");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("searchByName() - should return empty list for no matches")
    void searchByName_shouldReturnEmptyList_whenNoMatch() {
        when(productRepository.findByNameContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        List<ProductResponseDTO> result = productService.searchByName("xyz");

        assertThat(result).isEmpty();
    }

    // ─────────────────────── getProducts() (paginated) ─────────────────────

    @Test
    @DisplayName("getProducts() - should return paginated product DTOs")
    void getProducts_shouldReturnPage() {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ProductResponseDTO> result = productService.getProducts(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");
    }
}
