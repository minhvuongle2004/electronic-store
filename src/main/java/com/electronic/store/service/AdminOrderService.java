package com.electronic.store.service;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Order;
import com.electronic.store.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AdminOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Lấy tất cả đơn hàng với phân trang và filter
     */
    public ApiResponse<Map<String, Object>> getAllOrders(
            Order.OrderStatus status,
            Order.PaymentStatus paymentStatus,
            Order.PaymentMethod paymentMethod,
            String search,
            int page,
            int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> ordersPage = orderRepository.findOrdersWithFilters(
                status, paymentStatus, paymentMethod, search, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("orders", ordersPage.getContent());
            result.put("currentPage", ordersPage.getNumber());
            result.put("totalPages", ordersPage.getTotalPages());
            result.put("totalElements", ordersPage.getTotalElements());
            result.put("hasNext", ordersPage.hasNext());
            result.put("hasPrevious", ordersPage.hasPrevious());

            return ApiResponse.success("Lấy danh sách đơn hàng thành công", result);

        } catch (Exception e) {
            logger.error("Error getting orders with filters", e);
            return ApiResponse.error("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Lấy chi tiết đơn hàng theo ID
     */
    public ApiResponse<Order> getOrderById(Long orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);

            if (orderOpt.isEmpty()) {
                return ApiResponse.error("Không tìm thấy đơn hàng");
            }

            Order order = orderOpt.get();

            // Force load lazy collections
            order.getOrderItems().size(); // Load order items
            order.getOrderItems().forEach(item -> {
                if (item.getProduct() != null) {
                    item.getProduct().getName(); // Load product info
                }
            });

            if (order.getUser() != null) {
                order.getUser().getUsername(); // Load user info
            }

            return ApiResponse.success("Lấy chi tiết đơn hàng thành công", order);

        } catch (Exception e) {
            logger.error("Error getting order by ID: {}", orderId, e);
            return ApiResponse.error("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Lấy chi tiết đơn hàng theo orderId (string)
     */
    public ApiResponse<Order> getOrderByOrderId(String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);

            if (orderOpt.isEmpty()) {
                return ApiResponse.error("Không tìm thấy đơn hàng");
            }

            Order order = orderOpt.get();

            // Force load lazy collections
            order.getOrderItems().size();
            order.getOrderItems().forEach(item -> {
                if (item.getProduct() != null) {
                    item.getProduct().getName();
                }
            });

            if (order.getUser() != null) {
                order.getUser().getUsername();
            }

            return ApiResponse.success("Lấy chi tiết đơn hàng thành công", order);

        } catch (Exception e) {
            logger.error("Error getting order by orderId: {}", orderId, e);
            return ApiResponse.error("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    public ApiResponse<Order> updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);

            if (orderOpt.isEmpty()) {
                return ApiResponse.error("Không tìm thấy đơn hàng");
            }

            Order order = orderOpt.get();
            Order.OrderStatus currentStatus = order.getStatus();

            // Validate status transition
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                return ApiResponse.error("Không thể chuyển từ trạng thái " + currentStatus + " sang " + newStatus);
            }

            order.setStatus(newStatus);

            Order updatedOrder = orderRepository.save(order);

            logger.info("Order status updated: orderId={}, oldStatus={}, newStatus={}",
                       orderId, currentStatus, newStatus);

            return ApiResponse.success("Cập nhật trạng thái đơn hàng thành công", updatedOrder);

        } catch (Exception e) {
            logger.error("Error updating order status: orderId={}, newStatus={}", orderId, newStatus, e);
            return ApiResponse.error("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Hủy đơn hàng
     */
    public ApiResponse<Order> cancelOrder(Long orderId, String reason) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);

            if (orderOpt.isEmpty()) {
                return ApiResponse.error("Không tìm thấy đơn hàng");
            }

            Order order = orderOpt.get();

            // Chỉ có thể hủy đơn hàng ở trạng thái PENDING hoặc SHIPPED (chưa hoàn thành)
            if (order.getStatus() != Order.OrderStatus.PENDING &&
                order.getStatus() != Order.OrderStatus.SHIPPED) {
                return ApiResponse.error("Không thể hủy đơn hàng ở trạng thái " + order.getStatus());
            }

            order.setStatus(Order.OrderStatus.CANCELED);

            // If payment was made, mark as refund needed
            if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
                order.setPaymentStatus(Order.PaymentStatus.FAILED); // Or create REFUND status
            }

            Order canceledOrder = orderRepository.save(order);

            logger.info("Order canceled: orderId={}, reason={}", orderId, reason);

            return ApiResponse.success("Hủy đơn hàng thành công", canceledOrder);

        } catch (Exception e) {
            logger.error("Error canceling order: orderId={}", orderId, e);
            return ApiResponse.error("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Thống kê đơn hàng
     */
    public ApiResponse<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Đếm theo status
            stats.put("totalOrders", orderRepository.count());
            stats.put("pendingOrders", orderRepository.countByStatus(Order.OrderStatus.PENDING));
            stats.put("shippedOrders", orderRepository.countByStatus(Order.OrderStatus.SHIPPED));
            stats.put("completedOrders", orderRepository.countByStatus(Order.OrderStatus.COMPLETED));
            stats.put("canceledOrders", orderRepository.countByStatus(Order.OrderStatus.CANCELED));

            // Tính doanh thu
            Double completedRevenue = orderRepository.getTotalRevenueByStatus(Order.OrderStatus.COMPLETED);
            Double shippedRevenue = orderRepository.getTotalRevenueByStatus(Order.OrderStatus.SHIPPED);

            stats.put("completedRevenue", completedRevenue != null ? completedRevenue : 0.0);
            stats.put("shippedRevenue", shippedRevenue != null ? shippedRevenue : 0.0);

            return ApiResponse.success("Lấy thống kê đơn hàng thành công", stats);

        } catch (Exception e) {
            logger.error("Error getting order statistics", e);
            return ApiResponse.error("Lỗi khi lấy thống kê đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Validate status transition rules
     */
    private boolean isValidStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return true; // No change
        }

        switch (currentStatus) {
            case PENDING:
                return newStatus == Order.OrderStatus.SHIPPED ||
                       newStatus == Order.OrderStatus.CANCELED;
            case SHIPPED:
                return newStatus == Order.OrderStatus.COMPLETED ||
                       newStatus == Order.OrderStatus.CANCELED;
            case COMPLETED:
            case CANCELED:
                return false; // Final states
            default:
                return false;
        }
    }
}