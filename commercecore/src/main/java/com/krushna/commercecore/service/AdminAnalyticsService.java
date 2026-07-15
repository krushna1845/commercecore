package com.krushna.commercecore.service;

import com.krushna.commercecore.dto.AdminAnalyticsDTO;
import com.krushna.commercecore.model.Order;
import com.krushna.commercecore.model.Product;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminAnalyticsDTO getAnalytics() {
        AdminAnalyticsDTO analytics = new AdminAnalyticsDTO();

        // Revenue Metrics
        analytics.setRevenue(calculateRevenueMetrics());

        // Order Metrics
        analytics.setOrders(calculateOrderMetrics());

        // User Metrics
        analytics.setUsers(calculateUserMetrics());

        // Product Metrics
        analytics.setProducts(calculateProductMetrics());

        // Inventory Metrics
        analytics.setInventory(calculateInventoryMetrics());

        // Sales Metrics
        analytics.setSales(calculateSalesMetrics());

        // Growth Metrics
        analytics.setGrowth(calculateGrowthMetrics());

        // Conversion Metrics
        analytics.setConversion(calculateConversionMetrics());

        // Top Customers
        analytics.setTopCustomers(getTopCustomers());

        // Best Sellers
        analytics.setBestSellers(getBestSellers());

        // Monthly Data
        analytics.setMonthlyRevenue(getMonthlyRevenue());
        analytics.setMonthlyOrders(getMonthlyOrders());
        analytics.setMonthlyUsers(getMonthlyUsers());

        return analytics;
    }

    private AdminAnalyticsDTO.RevenueMetrics calculateRevenueMetrics() {
        AdminAnalyticsDTO.RevenueMetrics metrics = new AdminAnalyticsDTO.RevenueMetrics();
        
        List<Order> allOrders = orderRepository.findAll();
        double totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        
        LocalDate now = LocalDate.now();
        LocalDate currentMonthStart = now.withDayOfMonth(1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        
        double currentMonthRevenue = allOrders.stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isAfter(currentMonthStart.minusDays(1)))
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        
        double previousMonthRevenue = allOrders.stream()
                .filter(o -> {
                    LocalDate date = o.getCreatedAt().toLocalDate();
                    return date.isAfter(previousMonthStart.minusDays(1)) && date.isBefore(currentMonthStart);
                })
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        
        double revenueGrowth = previousMonthRevenue > 0 
                ? ((currentMonthRevenue - previousMonthRevenue) / previousMonthRevenue) * 100 
                : 0;
        
        double averageOrderValue = allOrders.size() > 0 ? totalRevenue / allOrders.size() : 0;
        
        metrics.setTotalRevenue(totalRevenue);
        metrics.setCurrentMonthRevenue(currentMonthRevenue);
        metrics.setPreviousMonthRevenue(previousMonthRevenue);
        metrics.setRevenueGrowth(revenueGrowth);
        metrics.setAverageOrderValue(averageOrderValue);
        
        return metrics;
    }

    private AdminAnalyticsDTO.OrderMetrics calculateOrderMetrics() {
        AdminAnalyticsDTO.OrderMetrics metrics = new AdminAnalyticsDTO.OrderMetrics();
        
        List<Order> allOrders = orderRepository.findAll();
        long totalOrders = allOrders.size();
        
        LocalDate now = LocalDate.now();
        LocalDate currentMonthStart = now.withDayOfMonth(1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        
        long currentMonthOrders = allOrders.stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isAfter(currentMonthStart.minusDays(1)))
                .count();
        
        long previousMonthOrders = allOrders.stream()
                .filter(o -> {
                    LocalDate date = o.getCreatedAt().toLocalDate();
                    return date.isAfter(previousMonthStart.minusDays(1)) && date.isBefore(currentMonthStart);
                })
                .count();
        
        double orderGrowth = previousMonthOrders > 0 
                ? ((currentMonthOrders - previousMonthOrders) / (double) previousMonthOrders) * 100 
                : 0;
        
        long pendingOrders = allOrders.stream().filter(o -> o.getStatus() == Order.Status.PENDING).count();
        long shippedOrders = allOrders.stream().filter(o -> o.getStatus() == Order.Status.SHIPPED).count();
        long deliveredOrders = 0; // DELIVERED status not available
        long cancelledOrders = allOrders.stream().filter(o -> o.getStatus() == Order.Status.CANCELLED).count();
        
        metrics.setTotalOrders(totalOrders);
        metrics.setCurrentMonthOrders(currentMonthOrders);
        metrics.setPreviousMonthOrders(previousMonthOrders);
        metrics.setOrderGrowth(orderGrowth);
        metrics.setPendingOrders(pendingOrders);
        metrics.setShippedOrders(shippedOrders);
        metrics.setDeliveredOrders(deliveredOrders);
        metrics.setCancelledOrders(cancelledOrders);
        
        return metrics;
    }

    private AdminAnalyticsDTO.UserMetrics calculateUserMetrics() {
        AdminAnalyticsDTO.UserMetrics metrics = new AdminAnalyticsDTO.UserMetrics();
        
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        
        // Since User doesn't have createdAt, use current month as baseline
        long currentMonthUsers = 0; // Cannot calculate without createdAt
        long previousMonthUsers = 0; // Cannot calculate without createdAt
        
        double userGrowth = 0; // Cannot calculate without createdAt
        
        long activeUsers = totalUsers; // Assume all users are active
        long inactiveUsers = 0;
        
        metrics.setTotalUsers(totalUsers);
        metrics.setCurrentMonthUsers(currentMonthUsers);
        metrics.setPreviousMonthUsers(previousMonthUsers);
        metrics.setUserGrowth(userGrowth);
        metrics.setActiveUsers(activeUsers);
        metrics.setInactiveUsers(inactiveUsers);
        
        return metrics;
    }

    private AdminAnalyticsDTO.ProductMetrics calculateProductMetrics() {
        AdminAnalyticsDTO.ProductMetrics metrics = new AdminAnalyticsDTO.ProductMetrics();
        
        List<Product> allProducts = productRepository.findAll();
        long totalProducts = allProducts.size();
        long activeProducts = totalProducts; // Assume all products are active
        long inactiveProducts = 0;
        long outOfStock = allProducts.stream().filter(p -> p.getStockQuantity() == 0).count();
        
        metrics.setTotalProducts(totalProducts);
        metrics.setActiveProducts(activeProducts);
        metrics.setInactiveProducts(inactiveProducts);
        metrics.setOutOfStock(outOfStock);
        
        return metrics;
    }

    private AdminAnalyticsDTO.InventoryMetrics calculateInventoryMetrics() {
        AdminAnalyticsDTO.InventoryMetrics metrics = new AdminAnalyticsDTO.InventoryMetrics();
        
        List<Product> allProducts = productRepository.findAll();
        long totalInventory = allProducts.stream().mapToLong(Product::getStockQuantity).sum();
        long lowStock = allProducts.stream().filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() < 10).count();
        long overStock = allProducts.stream().filter(p -> p.getStockQuantity() > 100).count();
        double inventoryValue = allProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getStockQuantity())
                .sum();
        
        metrics.setTotalInventory(totalInventory);
        metrics.setLowStock(lowStock);
        metrics.setOverStock(overStock);
        metrics.setInventoryValue(inventoryValue);
        
        return metrics;
    }

    private AdminAnalyticsDTO.SalesMetrics calculateSalesMetrics() {
        AdminAnalyticsDTO.SalesMetrics metrics = new AdminAnalyticsDTO.SalesMetrics();
        
        List<Order> allOrders = orderRepository.findAll();
        double totalSales = allOrders.stream()
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        
        LocalDate now = LocalDate.now();
        LocalDate currentMonthStart = now.withDayOfMonth(1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        
        double currentMonthSales = allOrders.stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isAfter(currentMonthStart.minusDays(1)))
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        
        double previousMonthSales = allOrders.stream()
                .filter(o -> {
                    LocalDate date = o.getCreatedAt().toLocalDate();
                    return date.isAfter(previousMonthStart.minusDays(1)) && date.isBefore(currentMonthStart);
                })
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        
        double salesGrowth = previousMonthSales > 0 
                ? ((currentMonthSales - previousMonthSales) / previousMonthSales) * 100 
                : 0;
        
        metrics.setTotalSales(totalSales);
        metrics.setCurrentMonthSales(currentMonthSales);
        metrics.setPreviousMonthSales(previousMonthSales);
        metrics.setSalesGrowth(salesGrowth);
        
        return metrics;
    }

    private AdminAnalyticsDTO.GrowthMetrics calculateGrowthMetrics() {
        AdminAnalyticsDTO.GrowthMetrics metrics = new AdminAnalyticsDTO.GrowthMetrics();
        
        AdminAnalyticsDTO.RevenueMetrics revenue = calculateRevenueMetrics();
        AdminAnalyticsDTO.OrderMetrics orders = calculateOrderMetrics();
        AdminAnalyticsDTO.UserMetrics users = calculateUserMetrics();
        AdminAnalyticsDTO.ConversionMetrics conversion = calculateConversionMetrics();
        
        metrics.setRevenueGrowth(revenue.getRevenueGrowth());
        metrics.setOrderGrowth(orders.getOrderGrowth());
        metrics.setUserGrowth(users.getUserGrowth());
        metrics.setConversionGrowth(conversion.getConversionGrowth());
        
        return metrics;
    }

    private AdminAnalyticsDTO.ConversionMetrics calculateConversionMetrics() {
        AdminAnalyticsDTO.ConversionMetrics metrics = new AdminAnalyticsDTO.ConversionMetrics();
        
        List<Order> allOrders = orderRepository.findAll();
        List<User> allUsers = userRepository.findAll();
        
        double totalConversion = allUsers.size() > 0 
                ? (allOrders.size() / (double) allUsers.size()) * 100 
                : 0;
        
        // Cannot calculate monthly conversion without User.createdAt
        double currentMonthConversion = 0;
        double previousMonthConversion = 0;
        double conversionGrowth = 0;
        
        metrics.setConversionRate(totalConversion);
        metrics.setCurrentMonthConversion(currentMonthConversion);
        metrics.setPreviousMonthConversion(previousMonthConversion);
        metrics.setConversionGrowth(conversionGrowth);
        
        return metrics;
    }

    private List<AdminAnalyticsDTO.TopCustomerDTO> getTopCustomers() {
        List<User> users = userRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        
        Map<Long, Double> userSpending = orders.stream()
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .collect(Collectors.groupingBy(
                        o -> o.getUser().getId(),
                        Collectors.summingDouble(o -> o.getTotalAmount().doubleValue())
                ));
        
        Map<Long, Long> userOrderCount = orders.stream()
                .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                .collect(Collectors.groupingBy(
                        o -> o.getUser().getId(),
                        Collectors.counting()
                ));
        
        return users.stream()
                .map(user -> {
                    AdminAnalyticsDTO.TopCustomerDTO dto = new AdminAnalyticsDTO.TopCustomerDTO();
                    dto.setUserId(user.getId());
                    dto.setUserName(user.getUsername());
                    dto.setEmail("user@example.com"); // Placeholder since User doesn't have email
                    dto.setTotalSpent(userSpending.getOrDefault(user.getId(), 0.0));
                    dto.setOrderCount(userOrderCount.getOrDefault(user.getId(), 0L));
                    return dto;
                })
                .sorted(Comparator.comparing(AdminAnalyticsDTO.TopCustomerDTO::getTotalSpent).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<AdminAnalyticsDTO.BestSellerDTO> getBestSellers() {
        List<Product> products = productRepository.findAll();
        
        return products.stream()
                .map(product -> {
                    AdminAnalyticsDTO.BestSellerDTO dto = new AdminAnalyticsDTO.BestSellerDTO();
                    dto.setProductId(product.getId());
                    dto.setProductName(product.getName());
                    dto.setImageUrl(product.getImageUrl());
                    
                    // Calculate units sold and revenue from order items
                    // This would need OrderItemRepository integration
                    dto.setUnitsSold(0);
                    dto.setTotalRevenue(0.0);
                    
                    return dto;
                })
                .sorted(Comparator.comparing(AdminAnalyticsDTO.BestSellerDTO::getTotalRevenue).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<AdminAnalyticsDTO.MonthlyDataDTO> getMonthlyRevenue() {
        List<AdminAnalyticsDTO.MonthlyDataDTO> monthlyData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            String month = yearMonth.format(formatter);
            
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();
            
            List<Order> monthOrders = orderRepository.findAll().stream()
                    .filter(o -> {
                        LocalDate date = o.getCreatedAt().toLocalDate();
                        return !date.isBefore(monthStart) && !date.isAfter(monthEnd);
                    })
                    .collect(Collectors.toList());
            
            double revenue = monthOrders.stream()
                    .filter(o -> o.getStatus() == Order.Status.PAID || o.getStatus() == Order.Status.SHIPPED)
                    .mapToDouble(o -> o.getTotalAmount().doubleValue())
                    .sum();
            
            AdminAnalyticsDTO.MonthlyDataDTO dto = new AdminAnalyticsDTO.MonthlyDataDTO();
            dto.setMonth(month);
            dto.setValue(revenue);
            dto.setCount(monthOrders.size());
            monthlyData.add(dto);
        }
        
        return monthlyData;
    }

    private List<AdminAnalyticsDTO.MonthlyDataDTO> getMonthlyOrders() {
        List<AdminAnalyticsDTO.MonthlyDataDTO> monthlyData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            String month = yearMonth.format(formatter);
            
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();
            
            long orderCount = orderRepository.findAll().stream()
                    .filter(o -> {
                        LocalDate date = o.getCreatedAt().toLocalDate();
                        return !date.isBefore(monthStart) && !date.isAfter(monthEnd);
                    })
                    .count();
            
            AdminAnalyticsDTO.MonthlyDataDTO dto = new AdminAnalyticsDTO.MonthlyDataDTO();
            dto.setMonth(month);
            dto.setValue(0);
            dto.setCount(orderCount);
            monthlyData.add(dto);
        }
        
        return monthlyData;
    }

    private List<AdminAnalyticsDTO.MonthlyDataDTO> getMonthlyUsers() {
        List<AdminAnalyticsDTO.MonthlyDataDTO> monthlyData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            String month = yearMonth.format(formatter);
            
            // Cannot calculate without User.createdAt
            long userCount = 0;
            
            AdminAnalyticsDTO.MonthlyDataDTO dto = new AdminAnalyticsDTO.MonthlyDataDTO();
            dto.setMonth(month);
            dto.setValue(0);
            dto.setCount(userCount);
            monthlyData.add(dto);
        }
        
        return monthlyData;
    }
}
