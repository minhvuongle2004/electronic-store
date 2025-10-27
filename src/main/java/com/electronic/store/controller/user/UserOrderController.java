package com.electronic.store.controller.user;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Order;
import com.electronic.store.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserOrderController {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Lấy danh sách đơn hàng của user hiện tại
     * GET /api/user/orders?page=0&size=10
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<Order>>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        try {
            // Lấy userId từ session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Vui lòng đăng nhập để xem lịch sử đơn hàng"));
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable);

            // Force lazy loading để tránh lỗi serialization
            orders.getContent().forEach(order -> {
                if (order.getOrderItems() != null) {
                    order.getOrderItems().size(); // trigger lazy loading
                    order.getOrderItems().forEach(item -> {
                        if (item.getProduct() != null) {
                            item.getProduct().getName(); // trigger product loading
                        }
                    });
                }
                if (order.getPayments() != null) {
                    order.getPayments().size(); // trigger lazy loading
                }
            });

            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đơn hàng thành công", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage()));
        }
    }

    /**
     * Lấy chi tiết đơn hàng theo ID
     * GET /api/user/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Order>> getOrderDetail(
            @PathVariable String orderId,
            HttpSession session) {
        try {
            // Lấy userId từ session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Vui lòng đăng nhập để xem chi tiết đơn hàng"));
            }

            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Order order = orderOpt.get();
            // Kiểm tra đơn hàng có thuộc về user hiện tại không
            if (!order.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("Bạn không có quyền xem đơn hàng này"));
            }

            // Force lazy loading như ở getUserOrders
            if (order.getOrderItems() != null) {
                order.getOrderItems().size();
                order.getOrderItems().forEach(item -> {
                    if (item.getProduct() != null) {
                        item.getProduct().getName();
                    }
                });
            }
            if (order.getPayments() != null) {
                order.getPayments().size();
            }

            return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết đơn hàng thành công", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage()));
        }
    }

    /**
     * Lấy đơn hàng theo trạng thái
     * GET /api/user/orders/status/{status}?page=0&size=10
     */
    @GetMapping("/status/{status}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<Order>>> getUserOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        try {
            // Lấy userId từ session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Vui lòng đăng nhập để xem lịch sử đơn hàng"));
            }

            // Chuyển đổi string thành enum
            Order.OrderStatus orderStatus;
            try {
                orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Trạng thái đơn hàng không hợp lệ"));
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderRepository.findByUserIdAndStatusOrderByOrderDateDesc(userId, orderStatus, pageable);

            return ResponseEntity.ok(ApiResponse.success("Lấy đơn hàng theo trạng thái thành công", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy đơn hàng theo trạng thái: " + e.getMessage()));
        }
    }

    /**
     * Hủy đơn hàng (chỉ cho đơn hàng PENDING)
     * PUT /api/user/orders/{orderId}/cancel
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @PathVariable String orderId,
            HttpSession session) {
        try {
            // Lấy userId từ session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Vui lòng đăng nhập để hủy đơn hàng"));
            }

            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Order order = orderOpt.get();
            // Kiểm tra đơn hàng có thuộc về user hiện tại không
            if (!order.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("Bạn không có quyền hủy đơn hàng này"));
            }

            // Chỉ cho phép hủy đơn hàng PENDING
            if (order.getStatus() != Order.OrderStatus.PENDING) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Chỉ có thể hủy đơn hàng đang chờ xử lý"));
            }

            // Cập nhật trạng thái đơn hàng
            order.setStatus(Order.OrderStatus.CANCELED);
            orderRepository.save(order);

            return ResponseEntity.ok(ApiResponse.success("Hủy đơn hàng thành công", "Đơn hàng đã được hủy"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi hủy đơn hàng: " + e.getMessage()));
        }
    }
}