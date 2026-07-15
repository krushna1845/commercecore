package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.SellerAnalyticsDTO;
import com.krushna.commercecore.model.OrderItem;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.OrderItemRepository;
import com.krushna.commercecore.repository.PayoutRepository;
import com.krushna.commercecore.repository.ProductRepository;
import com.krushna.commercecore.repository.UserRepository;
import com.krushna.commercecore.model.Payout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerAnalyticsService {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PayoutRepository payoutRepository;

    public SellerAnalyticsDTO getSellerAnalytics(Long sellerId) {
        SellerAnalyticsDTO analytics = new SellerAnalyticsDTO();
        List<OrderItem> sellerItems = orderItemRepository.findBySellerId(sellerId);
        List<Product> sellerProducts = productRepository.findBySeller(
                userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found")));
        List<Payout> sellerPayouts = payoutRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);

        analytics.setRevenue(calculateRevenueMetrics(sellerItems, sellerPayouts));
        analytics.setOrders(calculateOrderMetrics(sellerItems));
        analytics.setProducts(calculateProductMetrics(sellerProducts, sellerItems));
        analytics.setInventory(calculateInventoryMetrics(sellerProducts));
        analytics.setRecentOrders(getRecentOrders(sellerItems));
        analytics.setTopProducts(getTopProducts(sellerProducts, sellerItems));
        analytics.setMonthlyRevenue(getMonthlyRevenue(sellerItems));
        analytics.setLowStockProducts(getLowStockProducts(sellerProducts));
        return analytics;
    }

    private SellerAnalyticsDTO.RevenueMetrics calculateRevenueMetrics(List<OrderItem> items, List<Payout> payouts) {
        SellerAnalyticsDTO.RevenueMetrics metrics = new SellerAnalyticsDTO.RevenueMetrics();
        double totalRevenue = sumRevenue(items, false);
        double currentMonthRevenue = sumRevenue(items, true);
        
        double totalEarnings = totalRevenue * 0.90; // After 10% platform fee
        double withdrawnAmount = payouts.stream()
                .filter(p -> p.getStatus() != Payout.Status.FAILED)
                .mapToDouble(Payout::getAmount)
                .sum();
                
        double availablePayout = totalEarnings - withdrawnAmount;

        metrics.setTotalRevenue(totalRevenue);
        metrics.setCurrentMonthRevenue(currentMonthRevenue);
        metrics.setPendingPayout(payouts.stream().filter(p -> p.getStatus() == Payout.Status.PENDING).mapToDouble(Payout::getAmount).sum());
        metrics.setAvailablePayout(Math.max(0, availablePayout));
        return metrics;
    }

    private double sumRevenue(List<OrderItem> items, boolean currentMonthOnly) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        return items.stream()
                .filter(i -> isPaidOrBeyond(i.getStatus()))
                .filter(i -> !currentMonthOnly || !i.getOrder().getCreatedAt().toLocalDate().isBefore(monthStart))
                .mapToDouble(i -> i.getPriceAtPurchase().doubleValue() * i.getQuantity())
                .sum();
    }

    private boolean isPaidOrBeyond(OrderItem.ItemStatus status) {
        return status == OrderItem.ItemStatus.PAID
                || status == OrderItem.ItemStatus.SHIPPED
                || status == OrderItem.ItemStatus.DELIVERED;
    }

    private SellerAnalyticsDTO.OrderMetrics calculateOrderMetrics(List<OrderItem> items) {
        SellerAnalyticsDTO.OrderMetrics metrics = new SellerAnalyticsDTO.OrderMetrics();
        metrics.setTotalOrders(items.size());
        metrics.setPendingOrders(items.stream().filter(i -> i.getStatus() == OrderItem.ItemStatus.PENDING).count());
        metrics.setShippedOrders(items.stream().filter(i -> i.getStatus() == OrderItem.ItemStatus.SHIPPED).count());
        metrics.setDeliveredOrders(items.stream().filter(i -> i.getStatus() == OrderItem.ItemStatus.DELIVERED).count());
        metrics.setCancelledOrders(items.stream().filter(i -> i.getStatus() == OrderItem.ItemStatus.CANCELLED).count());
        return metrics;
    }

    private SellerAnalyticsDTO.ProductMetrics calculateProductMetrics(List<Product> products, List<OrderItem> items) {
        SellerAnalyticsDTO.ProductMetrics metrics = new SellerAnalyticsDTO.ProductMetrics();
        if (products == null) {
            metrics.setTotalProducts(0);
            metrics.setActiveProducts(0);
            metrics.setInactiveProducts(0);
            metrics.setItemsSold(0);
            return metrics;
        }
        long itemsSold = items.stream()
                .filter(i -> isPaidOrBeyond(i.getStatus()))
                .mapToLong(OrderItem::getQuantity)
                .sum();
        metrics.setTotalProducts(products.size());
        metrics.setActiveProducts(products.stream().filter(Product::isInStock).count());
        metrics.setInactiveProducts(products.size() - metrics.getActiveProducts());
        metrics.setItemsSold(itemsSold);
        return metrics;
    }

    private SellerAnalyticsDTO.InventoryMetrics calculateInventoryMetrics(List<Product> products) {
        SellerAnalyticsDTO.InventoryMetrics metrics = new SellerAnalyticsDTO.InventoryMetrics();
        if (products == null) {
            metrics.setTotalInventory(0);
            metrics.setLowStock(0);
            return metrics;
        }
        metrics.setTotalInventory(products.stream().mapToInt(Product::getStockQuantity).sum());
        metrics.setLowStock(products.stream()
                .filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() < LOW_STOCK_THRESHOLD)
                .count());
        return metrics;
    }

    private List<SellerAnalyticsDTO.SellerOrderDTO> getRecentOrders(List<OrderItem> items) {
        return items.stream().limit(10).map(item -> {
            SellerAnalyticsDTO.SellerOrderDTO dto = new SellerAnalyticsDTO.SellerOrderDTO();
            dto.setOrderId(item.getOrder().getId());
            dto.setOrderItemId(item.getId());
            dto.setOrderNumber("ORD-" + item.getOrder().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setCustomerName(item.getOrder().getUser().getUsername());
            dto.setTotalAmount(item.getPriceAtPurchase().doubleValue() * item.getQuantity());
            dto.setStatus(item.getStatus().name());
            dto.setCreatedAt(item.getOrder().getCreatedAt().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<SellerAnalyticsDTO.SellerProductDTO> getTopProducts(List<Product> products, List<OrderItem> items) {
        if (products == null) return new ArrayList<>();

        Map<Long, Long> soldMap = items.stream()
                .filter(i -> isPaidOrBeyond(i.getStatus()))
                .collect(Collectors.groupingBy(i -> i.getProduct().getId(), Collectors.summingLong(OrderItem::getQuantity)));

        Map<Long, Double> revenueMap = items.stream()
                .filter(i -> isPaidOrBeyond(i.getStatus()))
                .collect(Collectors.groupingBy(
                        i -> i.getProduct().getId(),
                        Collectors.summingDouble(i -> i.getPriceAtPurchase().doubleValue() * i.getQuantity())));

        return products.stream()
                .sorted((a, b) -> Long.compare(
                        soldMap.getOrDefault(b.getId(), 0L),
                        soldMap.getOrDefault(a.getId(), 0L)))
                .limit(5)
                .map(product -> {
                    SellerAnalyticsDTO.SellerProductDTO dto = new SellerAnalyticsDTO.SellerProductDTO();
                    dto.setProductId(product.getId());
                    dto.setProductName(product.getName());
                    dto.setImageUrl(product.getImageUrl());
                    dto.setPrice(product.getPrice());
                    dto.setStockQuantity(product.getStockQuantity());
                    dto.setUnitsSold(soldMap.getOrDefault(product.getId(), 0L));
                    dto.setTotalRevenue(revenueMap.getOrDefault(product.getId(), 0.0));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<SellerAnalyticsDTO.MonthlyDataDTO> getMonthlyRevenue(List<OrderItem> items) {
        List<SellerAnalyticsDTO.MonthlyDataDTO> monthlyData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();

            List<OrderItem> monthItems = items.stream()
                    .filter(item -> {
                        LocalDate date = item.getOrder().getCreatedAt().toLocalDate();
                        return !date.isBefore(monthStart) && !date.isAfter(monthEnd);
                    })
                    .filter(item -> isPaidOrBeyond(item.getStatus()))
                    .collect(Collectors.toList());

            double revenue = monthItems.stream()
                    .mapToDouble(item -> item.getPriceAtPurchase().doubleValue() * item.getQuantity())
                    .sum();

            SellerAnalyticsDTO.MonthlyDataDTO dto = new SellerAnalyticsDTO.MonthlyDataDTO();
            dto.setMonth(yearMonth.format(formatter));
            dto.setValue(revenue);
            dto.setCount(monthItems.size());
            monthlyData.add(dto);
        }
        return monthlyData;
    }

    private List<SellerAnalyticsDTO.LowStockProductDTO> getLowStockProducts(List<Product> products) {
        if (products == null) return new ArrayList<>();
        return products.stream()
                .filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() < LOW_STOCK_THRESHOLD)
                .map(p -> {
                    SellerAnalyticsDTO.LowStockProductDTO dto = new SellerAnalyticsDTO.LowStockProductDTO();
                    dto.setProductId(p.getId());
                    dto.setProductName(p.getName());
                    dto.setStockQuantity(p.getStockQuantity());
                    dto.setImageUrl(p.getImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
