package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.ProductRequestDTO;
import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        return productService.save(dto);
    }


    @GetMapping
    public Page<ProductResponseDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy) {

        return productService.getProducts(page, size, sortBy);
    }


    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getApprovedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProduct(@PathVariable Long id){
        return productService.getById(id);
    }

    @GetMapping("/search")
    public List<ProductResponseDTO> search(@RequestParam String name){
        return productService.searchByName(name);
    }

    @GetMapping("/compare")
    public List<ProductResponseDTO> compareProducts(@RequestParam List<Long> ids) {
        return productService.compareProducts(ids);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto){
        ProductResponseDTO updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
