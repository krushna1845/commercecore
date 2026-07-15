package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.SellerOrderItemDTO;
import com.krushna.commercecore.model.OrderItem;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.OrderItemRepository;
import com.krushna.commercecore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerOrderService {

    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public List<SellerOrderItemDTO> getSellerOrderItems(Long sellerId) {
        return orderItemRepository.findBySellerId(sellerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SellerOrderItemDTO updateItemStatus(Long sellerId, Long orderItemId, String statusStr) {
        OrderItem item = orderItemRepository.findByIdAndSellerId(orderItemId, sellerId)
                .orElseThrow(() -> new RuntimeException("Order item not found or access denied"));

        OrderItem.ItemStatus newStatus;
        try {
            newStatus = OrderItem.ItemStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + statusStr);
        }

        if (newStatus == OrderItem.ItemStatus.PENDING || newStatus == OrderItem.ItemStatus.PAID) {
            throw new RuntimeException("Cannot set status back to " + newStatus);
        }

        item.setStatus(newStatus);
        orderItemRepository.save(item);
        return toDto(item);
    }

    @Transactional
    public SellerOrderItemDTO updateTrackingInfo(Long sellerId, Long orderItemId, String trackingNumber, String courierName) {
        OrderItem item = orderItemRepository.findByIdAndSellerId(orderItemId, sellerId)
                .orElseThrow(() -> new RuntimeException("Order item not found or access denied"));
        
        item.setTrackingNumber(trackingNumber);
        item.setCourierName(courierName);
        item.setStatus(OrderItem.ItemStatus.SHIPPED); // Auto-advance to shipped
        
        orderItemRepository.save(item);
        return toDto(item);
    }

    private SellerOrderItemDTO toDto(OrderItem item) {
        SellerOrderItemDTO dto = new SellerOrderItemDTO();
        dto.setOrderItemId(item.getId());
        dto.setOrderId(item.getOrder().getId());
        dto.setOrderNumber("ORD-" + item.getOrder().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductImageUrl(item.getProduct().getImageUrl());
        dto.setQuantity(item.getQuantity());
        dto.setLineTotal(item.getPriceAtPurchase().doubleValue() * item.getQuantity());
        dto.setStatus(item.getStatus().name());
        dto.setCustomerName(item.getOrder().getUser().getUsername());
        dto.setCreatedAt(item.getOrder().getCreatedAt().toString());
        dto.setTrackingNumber(item.getTrackingNumber());
        dto.setCourierName(item.getCourierName());
        return dto;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
