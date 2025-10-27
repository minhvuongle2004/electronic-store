package com.electronic.store.controller.admin;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Order;
import com.electronic.store.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    /**
     * Hiển thị trang quản lý đơn hàng
     * GET /admin/orders
     */
    @GetMapping
    public String showOrderManagement() {
        return "admin/order/index";
    }

    /**
     * Hiển thị trang chi tiết đơn hàng
     * GET /admin/orders/{orderId}/detail
     */
    @GetMapping("/{orderId}/detail")
    public String showOrderDetail(@PathVariable Long orderId) {
        return "admin/order/detail";
    }

    /**
     * API: Lấy danh sách đơn hàng với filter và phân trang
     * GET /admin/orders/api/list
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Convert string parameters to enums
            Order.OrderStatus orderStatus = null;
            if (status != null && !status.isEmpty()) {
                orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            }

            Order.PaymentStatus paymentStatusEnum = null;
            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                paymentStatusEnum = Order.PaymentStatus.valueOf(paymentStatus.toUpperCase());
            }

            Order.PaymentMethod paymentMethodEnum = null;
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                paymentMethodEnum = Order.PaymentMethod.valueOf(paymentMethod.toUpperCase());
            }

            ApiResponse<Map<String, Object>> result = adminOrderService.getAllOrders(
                orderStatus, paymentStatusEnum, paymentMethodEnum, search, page, size);

            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tham số không hợp lệ: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * API: Lấy chi tiết đơn hàng theo ID
     * GET /admin/orders/api/{orderId}
     */
    @GetMapping("/api/{orderId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        ApiResponse<Order> result = adminOrderService.getOrderById(orderId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * API: Lấy chi tiết đơn hàng theo orderId (string)
     * GET /admin/orders/api/search/{orderId}
     */
    @GetMapping("/api/search/{orderId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Order>> getOrderByOrderId(@PathVariable String orderId) {
        ApiResponse<Order> result = adminOrderService.getOrderByOrderId(orderId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * API: Cập nhật trạng thái đơn hàng
     * PUT /admin/orders/api/{orderId}/status
     */
    @PutMapping("/api/{orderId}/status")
    @ResponseBody
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestData) {

        try {
            String statusStr = requestData.get("status");
            if (statusStr == null || statusStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Trạng thái không được để trống"));
            }

            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            ApiResponse<Order> result = adminOrderService.updateOrderStatus(orderId, newStatus);

            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Trạng thái không hợp lệ: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * API: Hủy đơn hàng
     * PUT /admin/orders/api/{orderId}/cancel
     */
    @PutMapping("/api/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody(required = false) Map<String, String> requestData) {

        String reason = requestData != null ? requestData.get("reason") : "Admin canceled";
        ApiResponse<Order> result = adminOrderService.cancelOrder(orderId, reason);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * API: Lấy thống kê đơn hàng
     * GET /admin/orders/api/statistics
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderStatistics() {
        ApiResponse<Map<String, Object>> result = adminOrderService.getOrderStatistics();

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * API: Test endpoint
     * GET /admin/orders/api/test
     */
    @GetMapping("/api/test")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Map.of("status", "OK", "message", "Admin Order Controller is working"));
    }
}