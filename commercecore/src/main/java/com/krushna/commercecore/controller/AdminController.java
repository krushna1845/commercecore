package com.krushna.commercecore.controller;

import com.krushna.commercecore.dto.ProductRequestDTO;
import com.krushna.commercecore.dto.ProductResponseDTO;
import com.krushna.commercecore.service.ProductService;
import com.krushna.commercecore.service.CategoryService;
import com.krushna.commercecore.dto.CategoryRequestDTO;
import com.krushna.commercecore.dto.CategoryResponseDTO;
import com.krushna.commercecore.model.Order;
import com.krushna.commercecore.model.ApprovalStatus;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.OrderRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public AdminController(ProductService productService,
                           CategoryService categoryService,
                           OrderRepository orderRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ── Products ──────────────────────────────────────────────────────────────

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.save(dto));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/pending")
    public ResponseEntity<List<ProductResponseDTO>> getPendingProducts() {
        return ResponseEntity.ok(productService.getPendingProducts());
    }

    @PutMapping("/products/{id}/approve")
    public ResponseEntity<ProductResponseDTO> approveProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        product.setApprovalStatus(ApprovalStatus.APPROVED);
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/products/{id}/reject")
    public ResponseEntity<ProductResponseDTO> rejectProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        product.setApprovalStatus(ApprovalStatus.REJECTED);
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(productService.getById(id));
    }

    // ── Categories ────────────────────────────────────────────────────────────

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @GetMapping("/orders")
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        List<Map<String, Object>> result = orderRepository.findAll().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .map(o -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", o.getId());
                    m.put("username", o.getUser().getUsername());
                    m.put("status", o.getStatus().name());
                    m.put("totalAmount", o.getTotalAmount());
                    m.put("createdAt", o.getCreatedAt());
                    m.put("itemCount", o.getItems().size());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        String statusStr = body.get("status");
        order.setStatus(Order.Status.valueOf(statusStr));
        orderRepository.save(order);
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", order.getId());
        resp.put("status", order.getStatus().name());
        return ResponseEntity.ok(resp);
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> result = userRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("username", u.getUsername());
                    m.put("role", u.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER");
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        List<Order> orders = orderRepository.findAll();
        long totalUsers = userRepository.count();
        long totalProducts = productService.getAll().size();

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() != Order.Status.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> recentOrders = orders.stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(5)
                .map(o -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", o.getId());
                    m.put("username", o.getUser().getUsername());
                    m.put("status", o.getStatus().name());
                    m.put("totalAmount", o.getTotalAmount());
                    m.put("createdAt", o.getCreatedAt());
                    m.put("itemCount", o.getItems().size());
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("totalRevenue", totalRevenue);
        data.put("totalOrders", (long) orders.size());
        data.put("totalProducts", totalProducts);
        data.put("totalUsers", totalUsers);
        data.put("recentOrders", recentOrders);

        return ResponseEntity.ok(data);
    }
}
