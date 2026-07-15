package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class SellerAnalyticsDTO {
    private RevenueMetrics revenue;
    private OrderMetrics orders;
    private ProductMetrics products;
    private InventoryMetrics inventory;
    private List<SellerOrderDTO> recentOrders;
    private List<SellerProductDTO> topProducts;
    private List<MonthlyDataDTO> monthlyRevenue;
    private List<LowStockProductDTO> lowStockProducts;

    public RevenueMetrics getRevenue() { return revenue; }
    public void setRevenue(RevenueMetrics revenue) { this.revenue = revenue; }
    public OrderMetrics getOrders() { return orders; }
    public void setOrders(OrderMetrics orders) { this.orders = orders; }
    public ProductMetrics getProducts() { return products; }
    public void setProducts(ProductMetrics products) { this.products = products; }
    public InventoryMetrics getInventory() { return inventory; }
    public void setInventory(InventoryMetrics inventory) { this.inventory = inventory; }
    public List<SellerOrderDTO> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<SellerOrderDTO> recentOrders) { this.recentOrders = recentOrders; }
    public List<SellerProductDTO> getTopProducts() { return topProducts; }
    public void setTopProducts(List<SellerProductDTO> topProducts) { this.topProducts = topProducts; }
    public List<MonthlyDataDTO> getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(List<MonthlyDataDTO> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    public List<LowStockProductDTO> getLowStockProducts() { return lowStockProducts; }
    public void setLowStockProducts(List<LowStockProductDTO> lowStockProducts) { this.lowStockProducts = lowStockProducts; }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueMetrics {
        private double totalRevenue;
        private double currentMonthRevenue;
        private double pendingPayout;
        private double availablePayout;

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public double getCurrentMonthRevenue() { return currentMonthRevenue; }
        public void setCurrentMonthRevenue(double currentMonthRevenue) { this.currentMonthRevenue = currentMonthRevenue; }
        public double getPendingPayout() { return pendingPayout; }
        public void setPendingPayout(double pendingPayout) { this.pendingPayout = pendingPayout; }
        public double getAvailablePayout() { return availablePayout; }
        public void setAvailablePayout(double availablePayout) { this.availablePayout = availablePayout; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderMetrics {
        private long totalOrders;
        private long pendingOrders;
        private long shippedOrders;
        private long deliveredOrders;
        private long cancelledOrders;

        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
        public long getShippedOrders() { return shippedOrders; }
        public void setShippedOrders(long shippedOrders) { this.shippedOrders = shippedOrders; }
        public long getDeliveredOrders() { return deliveredOrders; }
        public void setDeliveredOrders(long deliveredOrders) { this.deliveredOrders = deliveredOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductMetrics {
        private long totalProducts;
        private long activeProducts;
        private long inactiveProducts;
        private long itemsSold;

        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
        public long getActiveProducts() { return activeProducts; }
        public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }
        public long getInactiveProducts() { return inactiveProducts; }
        public void setInactiveProducts(long inactiveProducts) { this.inactiveProducts = inactiveProducts; }
        public long getItemsSold() { return itemsSold; }
        public void setItemsSold(long itemsSold) { this.itemsSold = itemsSold; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryMetrics {
        private long totalInventory;
        private long lowStock;

        public long getTotalInventory() { return totalInventory; }
        public void setTotalInventory(long totalInventory) { this.totalInventory = totalInventory; }
        public long getLowStock() { return lowStock; }
        public void setLowStock(long lowStock) { this.lowStock = lowStock; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerOrderDTO {
        private Long orderId;
        private Long orderItemId;
        private String orderNumber;
        private String productName;
        private String customerName;
        private double totalAmount;
        private String status;
        private String createdAt;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getOrderItemId() { return orderItemId; }
        public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerProductDTO {
        private Long productId;
        private String productName;
        private String imageUrl;
        private double price;
        private int stockQuantity;
        private long unitsSold;
        private double totalRevenue;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
        public long getUnitsSold() { return unitsSold; }
        public void setUnitsSold(long unitsSold) { this.unitsSold = unitsSold; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDataDTO {
        private String month;
        private double value;
        private long count;

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockProductDTO {
        private Long productId;
        private String productName;
        private int stockQuantity;
        private String imageUrl;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
