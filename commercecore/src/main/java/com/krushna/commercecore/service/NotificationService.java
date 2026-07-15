package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.NotificationDTO;
import com.krushna.commercecore.model.Notification;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public NotificationDTO createNotification(Long userId, String type, String title, String message, String link) {
        User user = new User();
        user.setId(userId);

        Notification notification = new Notification(user, type, title, message, link);
        notification = notificationRepository.save(notification);

        NotificationDTO dto = mapToDTO(notification);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/topic/notifications",
                dto
        );

        return dto;
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Long userId) {
        User user = new User();
        user.setId(userId);

        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        User user = new User();
        user.setId(userId);

        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        User user = new User();
        user.setId(userId);

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        User user = new User();
        user.setId(userId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        User user = new User();
        user.setId(userId);

        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        User user = new User();
        user.setId(userId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        notificationRepository.delete(notification);
    }

    @Transactional
    public void clearAllNotifications(Long userId) {
        User user = new User();
        user.setId(userId);
        notificationRepository.deleteByUser(user);
    }

    // Helper methods to create specific notification types
    public void notifyOrderUpdate(Long userId, String orderId, String status) {
        createNotification(
                userId,
                "ORDER_UPDATE",
                "Order Status Updated",
                String.format("Your order #%s status has been updated to %s", orderId, status),
                "/orders/" + orderId
        );
    }

    public void notifyPriceDrop(Long userId, String productName, double oldPrice, double newPrice) {
        createNotification(
                userId,
                "PRICE_DROP",
                "Price Drop Alert",
                String.format("%s price dropped from $%.2f to $%.2f", productName, oldPrice, newPrice),
                "/products"
        );
    }

    public void notifyWishlistAlert(Long userId, String productName, String alertType) {
        createNotification(
                userId,
                "WISHLIST_ALERT",
                "Wishlist Alert",
                String.format("%s: %s is now available", productName, alertType),
                "/wishlist"
        );
    }

    public void notifyPromotion(Long userId, String title, String message) {
        createNotification(
                userId,
                "PROMOTION",
                title,
                message,
                "/promotions"
        );
    }

    public void notifyCouponExpiry(Long userId, String couponCode) {
        createNotification(
                userId,
                "COUPON_EXPIRY",
                "Coupon Expiring Soon",
                String.format("Your coupon %s is expiring soon", couponCode),
                "/coupons"
        );
    }

    public void notifyFlashSale(Long userId, String saleName) {
        createNotification(
                userId,
                "FLASH_SALE",
                "Flash Sale Started",
                String.format("%s has started! Don't miss out", saleName),
                "/sales"
        );
    }

    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
