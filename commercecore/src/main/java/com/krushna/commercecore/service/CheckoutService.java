package com.krushna.commercecore.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.krushna.commercecore.dto.CheckoutRequestDTO;
import com.krushna.commercecore.dto.CheckoutResponseDTO;
import com.krushna.commercecore.dto.CheckoutSuccessDTO;
import com.krushna.commercecore.dto.DeliverySlotDTO;
import com.krushna.commercecore.dto.GiftWrapDTO;
import com.krushna.commercecore.dto.OrderSummaryDTO;
import com.krushna.commercecore.dto.ShippingMethodDTO;
import com.krushna.commercecore.model.Address;
import com.krushna.commercecore.model.Coupon;
import com.krushna.commercecore.model.DeliverySlot;
import com.krushna.commercecore.model.GiftWrap;
import com.krushna.commercecore.model.Order;
import com.krushna.commercecore.model.OrderItem;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.ShippingMethod;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.AddressRepository;
import com.krushna.commercecore.repository.CouponRepository;
import com.krushna.commercecore.repository.DeliverySlotRepository;
import com.krushna.commercecore.repository.GiftWrapRepository;
import com.krushna.commercecore.repository.OrderRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.ShippingMethodRepository;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final DeliverySlotRepository deliverySlotRepository;
    private final GiftWrapRepository giftWrapRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Transactional(readOnly = true)
    public CheckoutResponseDTO initializeCheckout(Long userId, List<CheckoutRequestDTO.CartItemDTO> items) {
        CheckoutResponseDTO response = new CheckoutResponseDTO();

        // Get available shipping methods
        List<ShippingMethod> shippingMethods = shippingMethodRepository.findByActiveTrueOrderByBasePriceAsc();
        response.setShippingMethods(shippingMethods.stream()
                .map(this::mapToShippingMethodDTO)
                .collect(Collectors.toList()));

        // Get available delivery slots for next 7 days
        List<DeliverySlot> deliverySlots = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 1; i <= 7; i++) {
            String date = LocalDate.now().plusDays(i).format(formatter);
            deliverySlots.addAll(deliverySlotRepository.findBySlotDateAndAvailableTrueOrderByStartTime(date));
        }
        response.setDeliverySlots(deliverySlots.stream()
                .map(this::mapToDeliverySlotDTO)
                .collect(Collectors.toList()));

        // Get available gift wraps
        List<GiftWrap> giftWraps = giftWrapRepository.findByActiveTrueOrderByPriceAsc();
        response.setGiftWraps(giftWraps.stream()
                .map(this::mapToGiftWrapDTO)
                .collect(Collectors.toList()));

        // Calculate order summary
        OrderSummaryDTO orderSummary = calculateOrderSummary(items, null, null, null, null, false, 0);
        response.setOrderSummary(orderSummary);

        return response;
    }

    @Transactional
    public OrderSummaryDTO calculateOrderSummary(
            List<CheckoutRequestDTO.CartItemDTO> items,
            Long shippingMethodId,
            Long deliverySlotId,
            Long giftWrapId,
            String couponCode,
            boolean gstInvoice,
            double gstRate) {

        OrderSummaryDTO summary = new OrderSummaryDTO();
        List<OrderSummaryDTO.OrderItemSummaryDTO> orderItems = new ArrayList<>();
        double subtotal = 0;

        // Calculate subtotal and build order items
        for (CheckoutRequestDTO.CartItemDTO item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            double itemTotal = product.getPrice() * item.getQuantity();
            subtotal += itemTotal;

            OrderSummaryDTO.OrderItemSummaryDTO orderItem = new OrderSummaryDTO.OrderItemSummaryDTO();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setTotalPrice(itemTotal);
            orderItems.add(orderItem);
        }

        summary.setSubtotal(subtotal);
        summary.setItems(orderItems);

        // Calculate shipping cost
        double shippingCost = 0;
        if (shippingMethodId != null) {
            ShippingMethod shippingMethod = shippingMethodRepository.findById(shippingMethodId)
                    .orElseThrow(() -> new RuntimeException("Shipping method not found"));
            shippingCost = shippingMethod.getBasePrice();
        }
        summary.setShippingCost(shippingCost);

        // Calculate gift wrap cost
        double giftWrapCost = 0;
        if (giftWrapId != null) {
            GiftWrap giftWrap = giftWrapRepository.findById(giftWrapId)
                    .orElseThrow(() -> new RuntimeException("Gift wrap not found"));
            giftWrapCost = giftWrap.getPrice() * items.size();
        }
        summary.setGiftWrapCost(giftWrapCost);

        // Calculate delivery slot cost
        double deliverySlotCost = 0;
        if (deliverySlotId != null) {
            DeliverySlot deliverySlot = deliverySlotRepository.findById(deliverySlotId)
                    .orElseThrow(() -> new RuntimeException("Delivery slot not found"));
            deliverySlotCost = deliverySlot.getPrice();
        }
        summary.setDeliverySlotCost(deliverySlotCost);

        // Calculate discount from coupon
        double couponDiscount = 0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponRepository.findValidCouponByCode(couponCode.toUpperCase(), LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

            couponDiscount = subtotal * (coupon.getDiscountPercentage() / 100.0);
            summary.setAppliedCouponCode(couponCode);
            summary.setCouponDiscount(couponDiscount);
        }
        summary.setDiscount(couponDiscount);

        // Calculate GST
        double gst = 0;
        if (gstInvoice) {
            gst = (subtotal + shippingCost + giftWrapCost + deliverySlotCost - couponDiscount) * (gstRate / 100);
        }
        summary.setGst(gst);

        // Calculate total
        double total = subtotal + shippingCost + giftWrapCost + deliverySlotCost - couponDiscount + gst;
        summary.setTotal(total);

        return summary;
    }

    @Transactional
    public CheckoutSuccessDTO placeOrder(Long userId, CheckoutRequestDTO request) {
        // Validate addresses exist and belong to the user
        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));
        
        // Security check: ensure address belongs to the user
        if (!shippingAddress.getUser().getId().equals(userId)) {
            throw new RuntimeException("Shipping address does not belong to this user");
        }
        
        Address billingAddress = null;
        if (request.getBillingAddressId() != null && !request.getBillingAddressId().equals(request.getShippingAddressId())) {
            billingAddress = addressRepository.findById(request.getBillingAddressId())
                    .orElseThrow(() -> new RuntimeException("Billing address not found"));
            
            // Security check: ensure billing address belongs to the user
            if (!billingAddress.getUser().getId().equals(userId)) {
                throw new RuntimeException("Billing address does not belong to this user");
            }
        } else {
            // If no billing address provided, use shipping address
            billingAddress = shippingAddress;
        }

        // Calculate order summary
        OrderSummaryDTO summary = calculateOrderSummary(
                request.getItems(),
                request.getShippingMethodId(),
                request.getDeliverySlotId(),
                request.getGiftWrapId(),
                request.getCouponCode(),
                request.isGstInvoice(),
                18 // GST rate for India
        );

        // Setup Stripe
        Stripe.apiKey = stripeSecretKey;
        String clientSecret = null;
        String paymentIntentId = "cod_" + System.currentTimeMillis();
        String paymentMethod = request.getPaymentMethod();

        if (summary.getTotal() > 0 && paymentMethod != null && !paymentMethod.equalsIgnoreCase("cod")) {
            long amountInCents = BigDecimal.valueOf(summary.getTotal())
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();

            try {
                PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency("inr") // Assuming INR
                        .putMetadata("userId", String.valueOf(userId));
                
                if (paymentMethod.equalsIgnoreCase("upi")) {
                    paramsBuilder.addPaymentMethodType("upi");
                } else {
                    paramsBuilder.addPaymentMethodType("card");
                }

                PaymentIntent intent = PaymentIntent.create(paramsBuilder.build());
                clientSecret = intent.getClientSecret();
                paymentIntentId = intent.getId();
            } catch (StripeException e) {
                throw new RuntimeException("Stripe payment initialization failed: " + e.getMessage(), e);
            }
        }

        // Create order
        Order order = new Order();
        User user = new User();
        user.setId(userId);
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        order.setTotalAmount(BigDecimal.valueOf(summary.getTotal()));
        order.setStatus(Order.Status.PENDING);
        order.setStripePaymentIntentId(paymentIntentId);

        // Save order
        order = orderRepository.save(order);

        // Create order items
        for (CheckoutRequestDTO.CartItemDTO item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(BigDecimal.valueOf(product.getPrice()));
            order.addItem(orderItem);
        }

        // Update delivery slot booking count
        if (request.getDeliverySlotId() != null) {
            DeliverySlot slot = deliverySlotRepository.findById(request.getDeliverySlotId())
                    .orElseThrow(() -> new RuntimeException("Delivery slot not found"));
            slot.setBookedCount(slot.getBookedCount() + 1);
            deliverySlotRepository.save(slot);
        }

        // Update coupon usage
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Coupon coupon = couponRepository.findValidCouponByCode(request.getCouponCode().toUpperCase(), LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
            coupon.incrementUsage();
            couponRepository.save(coupon);
        }

        return new CheckoutSuccessDTO(order, clientSecret, paymentIntentId);
    }

    private ShippingMethodDTO mapToShippingMethodDTO(ShippingMethod method) {
        ShippingMethodDTO dto = new ShippingMethodDTO();
        dto.setId(method.getId());
        dto.setName(method.getName());
        dto.setDescription(method.getDescription());
        dto.setBasePrice(method.getBasePrice());
        dto.setPricePerKg(method.getPricePerKg());
        dto.setEstimatedDaysMin(method.getEstimatedDaysMin());
        dto.setEstimatedDaysMax(method.getEstimatedDaysMax());
        return dto;
    }

    private DeliverySlotDTO mapToDeliverySlotDTO(DeliverySlot slot) {
        DeliverySlotDTO dto = new DeliverySlotDTO();
        dto.setId(slot.getId());
        dto.setSlotDate(slot.getSlotDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setAvailable(slot.isAvailable());
        dto.setCapacity(slot.getCapacity());
        dto.setBookedCount(slot.getBookedCount());
        dto.setPrice(slot.getPrice());
        return dto;
    }

    private GiftWrapDTO mapToGiftWrapDTO(GiftWrap giftWrap) {
        GiftWrapDTO dto = new GiftWrapDTO();
        dto.setId(giftWrap.getId());
        dto.setName(giftWrap.getName());
        dto.setDescription(giftWrap.getDescription());
        dto.setPrice(giftWrap.getPrice());
        dto.setImageUrl(giftWrap.getImageUrl());
        return dto;
    }
}
