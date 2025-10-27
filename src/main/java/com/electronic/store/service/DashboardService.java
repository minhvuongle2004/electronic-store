package com.electronic.store.service;

import com.electronic.store.entity.Order;
import com.electronic.store.entity.Product;
import com.electronic.store.entity.User;
import com.electronic.store.repository.CategoryRepository;
import com.electronic.store.repository.OrderRepository;
import com.electronic.store.repository.ProductRepository;
import com.electronic.store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DashboardService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy tổng quan dashboard
     */
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();

        // Tổng số danh mục
        long totalCategories = categoryRepository.count();

        // Tổng số sản phẩm
        long totalProducts = productRepository.count();

        // Tổng số đơn hàng
        long totalOrders = orderRepository.count();

        // Tổng số người dùng
        long totalUsers = userRepository.count();

        // Đơn hàng hôm nay
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long todayOrders = orderRepository.countByOrderDateBetween(startOfDay, endOfDay);

        // Doanh thu tháng này
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);
        Double monthlyRevenue = orderRepository.findRevenueByDateRange(startOfMonth, endOfMonth);
        if (monthlyRevenue == null) monthlyRevenue = 0.0;

        overview.put("totalCategories", totalCategories);
        overview.put("totalProducts", totalProducts);
        overview.put("totalOrders", totalOrders);
        overview.put("totalUsers", totalUsers);
        overview.put("todayOrders", todayOrders);
        overview.put("monthlyRevenue", monthlyRevenue);

        return overview;
    }

    /**
     * Lấy dữ liệu biểu đồ
     */
    public Map<String, Object> getChartData(String type, String period) {
        Map<String, Object> chartData = new HashMap<>();

        if ("daily".equals(type)) {
            // Biểu đồ theo ngày trong tháng
            chartData = getDailyChartData(period);
        } else if ("monthly".equals(type)) {
            // Biểu đồ theo tháng trong năm
            chartData = getMonthlyChartData(period);
        }

        return chartData;
    }

    /**
     * Lấy dữ liệu biểu đồ theo ngày
     */
    private Map<String, Object> getDailyChartData(String period) {
        Map<String, Object> data = new HashMap<>();

        // Xác định tháng (mặc định là tháng hiện tại)
        LocalDate targetMonth;
        if (period != null && !period.isEmpty()) {
            targetMonth = LocalDate.parse(period + "-01");
        } else {
            targetMonth = LocalDate.now().withDayOfMonth(1);
        }

        // Tạo danh sách các ngày trong tháng
        List<String> labels = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        int daysInMonth = targetMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = targetMonth.withDayOfMonth(day);
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = currentDate.atTime(23, 59, 59);

            labels.add(String.valueOf(day));

            // Đếm số đơn hàng
            long orderCount = orderRepository.countByOrderDateBetween(startOfDay, endOfDay);
            orderCounts.add(orderCount);

            // Tính doanh thu
            Double revenue = orderRepository.findRevenueByDateRange(startOfDay, endOfDay);
            revenues.add(revenue != null ? revenue : 0.0);
        }

        data.put("labels", labels);
        data.put("orderCounts", orderCounts);
        data.put("revenues", revenues);
        data.put("period", targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));

        return data;
    }

    /**
     * Lấy dữ liệu biểu đồ theo tháng
     */
    private Map<String, Object> getMonthlyChartData(String period) {
        Map<String, Object> data = new HashMap<>();

        // Xác định năm (mặc định là năm hiện tại)
        int targetYear;
        if (period != null && !period.isEmpty()) {
            targetYear = Integer.parseInt(period);
        } else {
            targetYear = LocalDate.now().getYear();
        }

        List<String> labels = Arrays.asList("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12");
        List<Long> orderCounts = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startOfMonth = LocalDate.of(targetYear, month, 1).atStartOfDay();
            LocalDateTime endOfMonth = LocalDate.of(targetYear, month, 1)
                    .withDayOfMonth(LocalDate.of(targetYear, month, 1).lengthOfMonth())
                    .atTime(23, 59, 59);

            // Đếm số đơn hàng
            long orderCount = orderRepository.countByOrderDateBetween(startOfMonth, endOfMonth);
            orderCounts.add(orderCount);

            // Tính doanh thu
            Double revenue = orderRepository.findRevenueByDateRange(startOfMonth, endOfMonth);
            revenues.add(revenue != null ? revenue : 0.0);
        }

        data.put("labels", labels);
        data.put("orderCounts", orderCounts);
        data.put("revenues", revenues);
        data.put("period", String.valueOf(targetYear));

        return data;
    }

    /**
     * Lấy top sản phẩm bán chạy
     */
    public Map<String, Object> getTopProducts(String type, String period, int limit) {
        Map<String, Object> data = new HashMap<>();

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if ("monthly".equals(type)) {
            LocalDate targetMonth;
            if (period != null && !period.isEmpty()) {
                targetMonth = LocalDate.parse(period + "-01");
            } else {
                targetMonth = LocalDate.now().withDayOfMonth(1);
            }

            startDate = targetMonth.atStartOfDay();
            endDate = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth()).atTime(23, 59, 59);
        }

        List<Object[]> topProductsData;
        if (startDate != null && endDate != null) {
            topProductsData = orderRepository.findTopProductsByDateRange(startDate, endDate);
        } else {
            topProductsData = orderRepository.findTopProducts();
        }

        // Mới:
        List<Map<String, Object>> products = new ArrayList<>();
        int count = 0;
        for (Object[] row : topProductsData) {
            if (count >= limit) break; // Áp dụng limit

            Map<String, Object> product = new HashMap<>();
            product.put("id", row[0]);
            product.put("name", row[1]);
            product.put("imageUrl", row[2]);
            product.put("totalQuantity", row[3]);
            product.put("totalRevenue", row[4]);
            products.add(product);
            count++;
        }

        data.put("products", products);
        data.put("period", period);
        data.put("type", type);

        return data;
    }

    /**
     * Lấy top khách hàng
     */
    public Map<String, Object> getTopCustomers(int limit) {
        Map<String, Object> data = new HashMap<>();

        List<Object[]> topCustomersData = orderRepository.findTopCustomers();

        // Mới:
        List<Map<String, Object>> customers = new ArrayList<>();
        int count = 0;
        for (Object[] row : topCustomersData) {
            if (count >= limit) break; // Áp dụng limit

            Map<String, Object> customer = new HashMap<>();
            customer.put("id", row[0]);
            customer.put("fullName", row[1]);
            customer.put("email", row[2]);
            customer.put("totalOrders", row[3]);
            customer.put("totalSpent", row[4]);
            customers.add(customer);
            count++;
        }

        data.put("customers", customers);

        return data;
    }

    /**
     * Lấy đơn hàng gần nhất
     */
    public Map<String, Object> getRecentOrders(int limit) {
        Map<String, Object> data = new HashMap<>();

        List<Order> recentOrders = orderRepository.findTop5ByOrderByOrderDateDesc();

        List<Map<String, Object>> orders = new ArrayList<>();
        for (Order order : recentOrders) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("id", order.getId());
            orderData.put("orderId", order.getOrderId());
            orderData.put("customerName", order.getShippingName());
            orderData.put("totalPrice", order.getTotalPrice());
            orderData.put("status", order.getStatus());
            orderData.put("paymentStatus", order.getPaymentStatus());
            orderData.put("orderDate", order.getOrderDate());
            orders.add(orderData);
        }

        data.put("orders", orders);

        return data;
    }

    /**
     * Lấy thống kê real-time
     */
    public Map<String, Object> getRealtimeStats() {
        Map<String, Object> stats = new HashMap<>();

        // Đơn hàng hôm nay
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long todayOrders = orderRepository.countByOrderDateBetween(startOfDay, endOfDay);

        // Doanh thu hôm nay
        Double todayRevenue = orderRepository.findRevenueByDateRange(startOfDay, endOfDay);
        if (todayRevenue == null) todayRevenue = 0.0;

        // Đơn hàng pending
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);

        // Sản phẩm sắp hết hàng (stock < 10)
        long lowStockProducts = productRepository.countByStockLessThan(10);

        stats.put("todayOrders", todayOrders);
        stats.put("todayRevenue", todayRevenue);
        stats.put("pendingOrders", pendingOrders);
        stats.put("lowStockProducts", lowStockProducts);
        stats.put("timestamp", System.currentTimeMillis());

        return stats;
    }
}