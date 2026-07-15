package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsDTO {
    private RevenueMetrics revenue;
    private OrderMetrics orders;
    private UserMetrics users;
    private ProductMetrics products;
    private InventoryMetrics inventory;
    private SalesMetrics sales;
    private GrowthMetrics growth;
    private ConversionMetrics conversion;
    private List<TopCustomerDTO> topCustomers;
    private List<BestSellerDTO> bestSellers;
    private List<MonthlyDataDTO> monthlyRevenue;
    private List<MonthlyDataDTO> monthlyOrders;
    private List<MonthlyDataDTO> monthlyUsers;

    public RevenueMetrics getRevenue() { return revenue; }
    public void setRevenue(RevenueMetrics revenue) { this.revenue = revenue; }
    public OrderMetrics getOrders() { return orders; }
    public void setOrders(OrderMetrics orders) { this.orders = orders; }
    public UserMetrics getUsers() { return users; }
    public void setUsers(UserMetrics users) { this.users = users; }
    public ProductMetrics getProducts() { return products; }
    public void setProducts(ProductMetrics products) { this.products = products; }
    public InventoryMetrics getInventory() { return inventory; }
    public void setInventory(InventoryMetrics inventory) { this.inventory = inventory; }
    public SalesMetrics getSales() { return sales; }
    public void setSales(SalesMetrics sales) { this.sales = sales; }
    public GrowthMetrics getGrowth() { return growth; }
    public void setGrowth(GrowthMetrics growth) { this.growth = growth; }
    public ConversionMetrics getConversion() { return conversion; }
    public void setConversion(ConversionMetrics conversion) { this.conversion = conversion; }
    public List<TopCustomerDTO> getTopCustomers() { return topCustomers; }
    public void setTopCustomers(List<TopCustomerDTO> topCustomers) { this.topCustomers = topCustomers; }
    public List<BestSellerDTO> getBestSellers() { return bestSellers; }
    public void setBestSellers(List<BestSellerDTO> bestSellers) { this.bestSellers = bestSellers; }
    public List<MonthlyDataDTO> getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(List<MonthlyDataDTO> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    public List<MonthlyDataDTO> getMonthlyOrders() { return monthlyOrders; }
    public void setMonthlyOrders(List<MonthlyDataDTO> monthlyOrders) { this.monthlyOrders = monthlyOrders; }
    public List<MonthlyDataDTO> getMonthlyUsers() { return monthlyUsers; }
    public void setMonthlyUsers(List<MonthlyDataDTO> monthlyUsers) { this.monthlyUsers = monthlyUsers; }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueMetrics {
        private double totalRevenue;
        private double currentMonthRevenue;
        private double previousMonthRevenue;
        private double revenueGrowth;
        private double averageOrderValue;

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public double getCurrentMonthRevenue() { return currentMonthRevenue; }
        public void setCurrentMonthRevenue(double currentMonthRevenue) { this.currentMonthRevenue = currentMonthRevenue; }
        public double getPreviousMonthRevenue() { return previousMonthRevenue; }
        public void setPreviousMonthRevenue(double previousMonthRevenue) { this.previousMonthRevenue = previousMonthRevenue; }
        public double getRevenueGrowth() { return revenueGrowth; }
        public void setRevenueGrowth(double revenueGrowth) { this.revenueGrowth = revenueGrowth; }
        public double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderMetrics {
        private long totalOrders;
        private long currentMonthOrders;
        private long previousMonthOrders;
        private double orderGrowth;
        private long pendingOrders;
        private long shippedOrders;
        private long deliveredOrders;
        private long cancelledOrders;

        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getCurrentMonthOrders() { return currentMonthOrders; }
        public void setCurrentMonthOrders(long currentMonthOrders) { this.currentMonthOrders = currentMonthOrders; }
        public long getPreviousMonthOrders() { return previousMonthOrders; }
        public void setPreviousMonthOrders(long previousMonthOrders) { this.previousMonthOrders = previousMonthOrders; }
        public double getOrderGrowth() { return orderGrowth; }
        public void setOrderGrowth(double orderGrowth) { this.orderGrowth = orderGrowth; }
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
    public static class UserMetrics {
        private long totalUsers;
        private long currentMonthUsers;
        private long previousMonthUsers;
        private double userGrowth;
        private long activeUsers;
        private long inactiveUsers;

        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        public long getCurrentMonthUsers() { return currentMonthUsers; }
        public void setCurrentMonthUsers(long currentMonthUsers) { this.currentMonthUsers = currentMonthUsers; }
        public long getPreviousMonthUsers() { return previousMonthUsers; }
        public void setPreviousMonthUsers(long previousMonthUsers) { this.previousMonthUsers = previousMonthUsers; }
        public double getUserGrowth() { return userGrowth; }
        public void setUserGrowth(double userGrowth) { this.userGrowth = userGrowth; }
        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
        public long getInactiveUsers() { return inactiveUsers; }
        public void setInactiveUsers(long inactiveUsers) { this.inactiveUsers = inactiveUsers; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductMetrics {
        private long totalProducts;
        private long activeProducts;
        private long inactiveProducts;
        private long outOfStock;

        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
        public long getActiveProducts() { return activeProducts; }
        public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }
        public long getInactiveProducts() { return inactiveProducts; }
        public void setInactiveProducts(long inactiveProducts) { this.inactiveProducts = inactiveProducts; }
        public long getOutOfStock() { return outOfStock; }
        public void setOutOfStock(long outOfStock) { this.outOfStock = outOfStock; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryMetrics {
        private long totalInventory;
        private long lowStock;
        private long overStock;
        private double inventoryValue;

        public long getTotalInventory() { return totalInventory; }
        public void setTotalInventory(long totalInventory) { this.totalInventory = totalInventory; }
        public long getLowStock() { return lowStock; }
        public void setLowStock(long lowStock) { this.lowStock = lowStock; }
        public long getOverStock() { return overStock; }
        public void setOverStock(long overStock) { this.overStock = overStock; }
        public double getInventoryValue() { return inventoryValue; }
        public void setInventoryValue(double inventoryValue) { this.inventoryValue = inventoryValue; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesMetrics {
        private double totalSales;
        private double currentMonthSales;
        private double previousMonthSales;
        private double salesGrowth;

        public double getTotalSales() { return totalSales; }
        public void setTotalSales(double totalSales) { this.totalSales = totalSales; }
        public double getCurrentMonthSales() { return currentMonthSales; }
        public void setCurrentMonthSales(double currentMonthSales) { this.currentMonthSales = currentMonthSales; }
        public double getPreviousMonthSales() { return previousMonthSales; }
        public void setPreviousMonthSales(double previousMonthSales) { this.previousMonthSales = previousMonthSales; }
        public double getSalesGrowth() { return salesGrowth; }
        public void setSalesGrowth(double salesGrowth) { this.salesGrowth = salesGrowth; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthMetrics {
        private double revenueGrowth;
        private double orderGrowth;
        private double userGrowth;
        private double conversionGrowth;

        public double getRevenueGrowth() { return revenueGrowth; }
        public void setRevenueGrowth(double revenueGrowth) { this.revenueGrowth = revenueGrowth; }
        public double getOrderGrowth() { return orderGrowth; }
        public void setOrderGrowth(double orderGrowth) { this.orderGrowth = orderGrowth; }
        public double getUserGrowth() { return userGrowth; }
        public void setUserGrowth(double userGrowth) { this.userGrowth = userGrowth; }
        public double getConversionGrowth() { return conversionGrowth; }
        public void setConversionGrowth(double conversionGrowth) { this.conversionGrowth = conversionGrowth; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversionMetrics {
        private double conversionRate;
        private double currentMonthConversion;
        private double previousMonthConversion;
        private double conversionGrowth;

        public double getConversionRate() { return conversionRate; }
        public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }
        public double getCurrentMonthConversion() { return currentMonthConversion; }
        public void setCurrentMonthConversion(double currentMonthConversion) { this.currentMonthConversion = currentMonthConversion; }
        public double getPreviousMonthConversion() { return previousMonthConversion; }
        public void setPreviousMonthConversion(double previousMonthConversion) { this.previousMonthConversion = previousMonthConversion; }
        public double getConversionGrowth() { return conversionGrowth; }
        public void setConversionGrowth(double conversionGrowth) { this.conversionGrowth = conversionGrowth; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomerDTO {
        private Long userId;
        private String userName;
        private String email;
        private double totalSpent;
        private long orderCount;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
        public long getOrderCount() { return orderCount; }
        public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestSellerDTO {
        private Long productId;
        private String productName;
        private String imageUrl;
        private double totalRevenue;
        private long unitsSold;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public long getUnitsSold() { return unitsSold; }
        public void setUnitsSold(long unitsSold) { this.unitsSold = unitsSold; }
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
}
