package com.example.web_organic.service;

import com.example.web_organic.entity.Order;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.response.DashboardStatsResponse;
import com.example.web_organic.repository.OrderRepository;
import com.example.web_organic.repository.ProductRepository;
import com.example.web_organic.repository.ProductVariantRepository;
import com.example.web_organic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariantRepository productVariantsRepository;
    public DashboardStatsResponse getDashboardStats() {
        // Xác định ngày đầu tháng hiện tại và tháng trước
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = today.withDayOfMonth(1);
        LocalDate firstDayOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1);

        LocalDateTime startOfPreviousMonth = firstDayOfPreviousMonth.atStartOfDay();
        LocalDateTime startOfCurrentMonth = firstDayOfCurrentMonth.atStartOfDay();
        LocalDateTime startOfNextMonth = firstDayOfCurrentMonth.plusMonths(1).atStartOfDay();
        // Lấy doanh thu
        BigDecimal previousMonthRevenue = orderRepository.getRevenueBetween(startOfPreviousMonth, startOfCurrentMonth);
        BigDecimal currentMonthRevenue = orderRepository.getRevenueBetween(startOfCurrentMonth, startOfNextMonth);

        // Đảm bảo không null
        currentMonthRevenue = currentMonthRevenue != null ? currentMonthRevenue : BigDecimal.ZERO;
        previousMonthRevenue = previousMonthRevenue != null ? previousMonthRevenue : BigDecimal.ZERO;

        // Tính tỷ lệ tăng trưởng doanh thu
        double revenueGrowthRate = 0.0;
        if (previousMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = currentMonthRevenue.subtract(previousMonthRevenue);
            revenueGrowthRate = change.divide(previousMonthRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            revenueGrowthRate = Math.round(revenueGrowthRate * 10.0) / 10.0;
        }

        // ✅ Đơn hàng
        int currentMonthOrders = orderRepository.countOrdersBetween(startOfCurrentMonth, startOfNextMonth);
        int previousMonthOrders = orderRepository.countOrdersBetween(startOfPreviousMonth, startOfCurrentMonth);

        double orderGrowthRate = 0.0;
        if (previousMonthOrders > 0) {
            orderGrowthRate = ((currentMonthOrders - previousMonthOrders) / (double) previousMonthOrders) * 100;
            orderGrowthRate = Math.round(orderGrowthRate * 10.0) / 10.0;
        }

        // Thống kê khách hàng
        int newCustomersCurrentMonth = userRepository.countNewCustomersBetween(startOfCurrentMonth, startOfNextMonth);
        int newCustomersPreviousMonth = userRepository.countNewCustomersBetween(startOfPreviousMonth, startOfCurrentMonth);

// Tính tăng trưởng
        double customerGrowthRate = 0.0;
        if (newCustomersPreviousMonth > 0) {
            customerGrowthRate = ((newCustomersCurrentMonth - newCustomersPreviousMonth) / (double)newCustomersPreviousMonth) * 100;
            customerGrowthRate = Math.round(customerGrowthRate * 10.0) / 10.0;
        }

// Lấy số lượng sản phẩm đã bán
        Integer soldProductsCurrentMonth = productRepository.countSoldProductsBetween(startOfCurrentMonth, startOfNextMonth);
        Integer soldProductsPreviousMonth = productRepository.countSoldProductsBetween(startOfPreviousMonth, startOfCurrentMonth);

// Null safety
        soldProductsCurrentMonth = soldProductsCurrentMonth != null ? soldProductsCurrentMonth : 0;
        soldProductsPreviousMonth = soldProductsPreviousMonth != null ? soldProductsPreviousMonth : 0;

// Tính tỷ lệ tăng trưởng
        double productGrowthRate = 0.0;
        if (soldProductsPreviousMonth > 0) {
            productGrowthRate = ((soldProductsCurrentMonth - soldProductsPreviousMonth) * 100.0) / soldProductsPreviousMonth;
            productGrowthRate = Math.round(productGrowthRate * 10.0) / 10.0;
        }


        // Trả về dữ liệu
        return DashboardStatsResponse.builder()
            .currentMonthRevenue(currentMonthRevenue)
            .previousMonthRevenue(previousMonthRevenue)
            .revenueGrowthRate(revenueGrowthRate)
            .currentMonthOrders(currentMonthOrders)
            .previousMonthOrders(previousMonthOrders)
            .orderGrowthRate(orderGrowthRate)
            .newCustomersCurrentMonth(newCustomersCurrentMonth)
            .newCustomersPreviousMonth(newCustomersPreviousMonth)
            .customerGrowthRate(customerGrowthRate)
            .hotProductsCurrentMonth(soldProductsCurrentMonth)
            .hotProductsPreviousMonth(soldProductsPreviousMonth)
            .hotProductGrowthRate(productGrowthRate)
            .build();
    }

    // Lấy dữ liệu cho biểu đồ doanh thu theo thời gian
    public List<Map<String, Object>> getDailyRevenue() {
        return orderRepository.getDailyRevenue();
    }

    public List<Map<String, Object>> getMonthlyRevenue() {
        return orderRepository.getMonthlyRevenue();
    }

    // Lấy dữ liệu cho biểu đồ trạng thái đơn hàng
    public List<Map<String, Object>> getOrderStatusDistribution() {
        List<Object[]> rawData = orderRepository.getOrderStatusDistributionRaw();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rawData) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", row[0] != null ? row[0].toString() : "Unknown");
            map.put("count", ((Number) row[1]).longValue());
            result.add(map);
        }

        return result;
    }

    // Lấy dữ liệu cho bảng đơn hàng gần đây
    public List<Map<String, Object>> getRecentOrders() {
        return orderRepository.getRecentOrders();
    }

    public List<User> getTop5Users() {
        return userRepository.findTop5AndStatusTrueByOrderByCreatedAtDesc();
    }

    // Lấy dữ liệu cho biểu đồ phân loại sản phẩm
    public List<Map<String, Object>> getProductCategoryDistribution() {
        return productRepository.getProductCategoryDistribution();
    }

    // Lấy dữ liệu cho biểu đồ sản phẩm bán chạy
    public List<Map<String, Object>> getHotProducts() {
        Pageable top5 = PageRequest.of(0, 5);
        return productRepository.getHotProducts(top5);
    }

    public List<Map<String, Object>> getStockAlerts() {
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> rawResults = productVariantsRepository.getStockAlerts(top5);

        return rawResults.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("variantId", row[0]);
            map.put("productName", row[1]);
            map.put("weight", row[2]);
            map.put("stockQuantity", row[3]);
            map.put("stockStatus", row[4]);
            return map;
        }).toList();
    }

    public List<Map<String, Object>> getCustomerDistributionByProvince() {
        return orderRepository.getCustomerDistributionByProvince();
    }

    public Map<String, Object> getRevenueChartData(String range) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data;
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        switch (range) {
            case "week":
                data = orderRepository.getWeeklyRevenue();
                break;
            case "month":
                data = orderRepository.getDailyRevenue();
                break;
            case "year":
                data = orderRepository.getMonthlyRevenues();
                break;
            default:
                data = orderRepository.getDailyRevenue();
        }

        for (Map<String, Object> row : data) {
            labels.add(row.get("date").toString());
            values.add(((Number) row.get("revenue")).doubleValue());
        }

        result.put("labels", labels);
        result.put("values", values);
        return result;
    }

    public Map<String, Object> getCustomDateRangeRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = orderRepository.getCustomDateRangeRevenue(startDate, endDate);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Map<String, Object> row : data) {
            labels.add(row.get("date").toString());
            values.add(((Number) row.get("revenue")).doubleValue());
        }

        result.put("labels", labels);
        result.put("values", values);
        return result;
    }
}
