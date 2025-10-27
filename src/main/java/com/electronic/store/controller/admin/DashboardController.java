package com.electronic.store.controller.admin;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Lấy tổng quan thống kê dashboard
     * GET /api/admin/dashboard/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardOverview() {
        try {
            Map<String, Object> overview = dashboardService.getDashboardOverview();
            return ResponseEntity.ok(ApiResponse.success("Lấy tổng quan thành công", overview));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi lấy tổng quan: " + e.getMessage()));
        }
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu và đơn hàng
     * GET /api/admin/dashboard/charts?type=daily|monthly&period=2024-01
     */
    @GetMapping("/charts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChartData(
            @RequestParam String type,
            @RequestParam(required = false) String period) {
        try {
            Map<String, Object> chartData = dashboardService.getChartData(type, period);
            return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu biểu đồ thành công", chartData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi lấy dữ liệu biểu đồ: " + e.getMessage()));
        }
    }

    /**
     * Lấy top sản phẩm bán chạy
     * GET /api/admin/dashboard/top-products?type=monthly&period=2024-01&limit=5
     */
    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopProducts(
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(required = false) String period,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            Map<String, Object> topProducts = dashboardService.getTopProducts(type, period, limit);
            return ResponseEntity.ok(ApiResponse.success("Lấy sản phẩm bán chạy thành công", topProducts));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi lấy sản phẩm bán chạy: " + e.getMessage()));
        }
    }

    /**
     * Lấy top khách hàng
     * GET /api/admin/dashboard/top-customers?limit=5
     */
    @GetMapping("/top-customers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopCustomers(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            Map<String, Object> topCustomers = dashboardService.getTopCustomers(limit);
            return ResponseEntity.ok(ApiResponse.success("Lấy top khách hàng thành công", topCustomers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi lấy top khách hàng: " + e.getMessage()));
        }
    }

    /**
     * Lấy 5 đơn hàng gần nhất
     * GET /api/admin/dashboard/recent-orders
     */
    @GetMapping("/recent-orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecentOrders() {
        try {
            Map<String, Object> recentOrders = dashboardService.getRecentOrders(5);
            return ResponseEntity.ok(ApiResponse.success("Lấy đơn hàng gần nhất thành công", recentOrders));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi lấy đơn hàng gần nhất: " + e.getMessage()));
        }
    }

    /**
     * Lấy thống kê real-time (cho WebSocket hoặc polling)
     * GET /api/admin/dashboard/realtime-stats
     */
    @GetMapping("/realtime-stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRealtimeStats() {
        try {
            Map<String, Object> stats = dashboardService.getRealtimeStats();
            return ResponseEntity.ok(ApiResponse.success("Lấy thống kê real-time thành công", stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi lấy thống kê real-time: " + e.getMessage()));
        }
    }
}