package com.krushna.commercecore.controller;

import com.krushna.commercecore.model.Order;
import com.krushna.commercecore.model.OrderItem;
import com.krushna.commercecore.repository.OrderRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.service.CartService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles incoming Stripe webhook events.
 *
 * Endpoint:  POST /payment/webhook
 * Auth:      None — Stripe sends events without a JWT.
 *            This endpoint is permitted in SecurityConfig.
 *
 * Events handled:
 *   payment_intent.succeeded  → Mark order PAID, clear the user's cart.
 *   payment_intent.payment_failed → Mark order FAILED.
 */
@RestController
@RequestMapping("/payment")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public WebhookController(OrderRepository orderRepository,
                             ProductRepository productRepository,
                             CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        // ── 1. Verify Stripe signature ──────────────────────────────────
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Stripe webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Stripe signature");
        }

        log.info("Received Stripe event: {}", event.getType());

        // ── 2. Handle payment_intent.succeeded ──────────────────────────
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = extractPaymentIntent(event);
            if (intent != null) {
                orderRepository.findByStripePaymentIntentId(intent.getId())
                        .ifPresentOrElse(order -> {
                            // Mark order as PAID
                            order.setStatus(Order.Status.PAID);
                            order.getItems().forEach(item -> item.setStatus(OrderItem.ItemStatus.PAID));
                            orderRepository.save(order);
                            log.info("Order #{} marked as PAID (PI: {})",
                                    order.getId(), intent.getId());

                            // Decrease stock quantities for ordered products
                            try {
                                updateStockQuantities(order);
                                log.info("Stock quantities updated for order #{}", order.getId());
                            } catch (Exception ex) {
                                log.error("Failed to update stock quantities for order #{}: {}",
                                        order.getId(), ex.getMessage());
                            }

                            // Clear the cart so the user can shop again
                            String username = order.getUser().getUsername();
                            try {
                                cartService.clearCart(username);
                                log.info("Cart cleared for user '{}'", username);
                            } catch (Exception ex) {
                                log.error("Failed to clear cart for user '{}': {}",
                                        username, ex.getMessage());
                            }
                        }, () -> log.warn("No order found for PaymentIntent: {}", intent.getId()));
            }
        }

        // ── 3. Handle payment_intent.payment_failed ─────────────────────
        if ("payment_intent.payment_failed".equals(event.getType())) {
            PaymentIntent intent = extractPaymentIntent(event);
            if (intent != null) {
                orderRepository.findByStripePaymentIntentId(intent.getId())
                        .ifPresentOrElse(order -> {
                            order.setStatus(Order.Status.FAILED);
                            orderRepository.save(order);
                            log.info("Order #{} marked as FAILED (PI: {})",
                                    order.getId(), intent.getId());
                        }, () -> log.warn("No order found for failed PaymentIntent: {}", intent.getId()));
            }
        }

        return ResponseEntity.ok("Webhook processed");
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    @Transactional
    private void updateStockQuantities(Order order) {
        order.getItems().forEach(orderItem -> {
            com.krushna.commercecore.model.Product product = orderItem.getProduct();
            int currentStock = product.getStockQuantity();
            int orderedQuantity = orderItem.getQuantity();
            
            if (currentStock >= orderedQuantity) {
                product.setStockQuantity(currentStock - orderedQuantity);
                productRepository.save(product);
                log.info("Decreased stock for product '{}' from {} to {}",
                        product.getName(), currentStock, product.getStockQuantity());
            } else {
                log.warn("Insufficient stock for product '{}'. Current: {}, Ordered: {}",
                        product.getName(), currentStock, orderedQuantity);
            }
        });
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        return (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);
    }
}
