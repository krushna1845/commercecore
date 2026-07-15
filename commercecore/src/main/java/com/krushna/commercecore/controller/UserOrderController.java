package com.krushna.commercecore.controller;

import com.krushna.commercecore.model.Order;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.OrderRepository;
import com.krushna.commercecore.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class UserOrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public UserOrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<List<Map<String, Object>>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Map<String, Object>> result = orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .map(o -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", o.getId());
                    m.put("status", o.getStatus().name());
                    m.put("totalAmount", o.getTotalAmount());
                    m.put("createdAt", o.getCreatedAt());
                    m.put("items", o.getItems().stream().map(item -> {
                        Map<String, Object> i = new HashMap<>();
                        i.put("productId", item.getProduct().getId());
                        i.put("productName", item.getProduct().getName());
                        i.put("quantity", item.getQuantity());
                        i.put("price", item.getPriceAtPurchase());
                        i.put("imageUrl", item.getProduct().getImageUrl());
                        i.put("trackingNumber", item.getTrackingNumber());
                        i.put("courierName", item.getCourierName());
                        return i;
                    }).collect(Collectors.toList()));
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
