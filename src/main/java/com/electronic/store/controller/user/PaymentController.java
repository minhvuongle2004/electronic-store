package com.electronic.store.controller.user;

import com.electronic.store.dto.request.PaymentRequest;
import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.dto.response.PaymentResponse;
import com.electronic.store.entity.Order;
import com.electronic.store.entity.Payment;
import com.electronic.store.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Tạo MoMo payment request
     * POST /api/payment/momo/create
     */
    @PostMapping("/momo/create")
    public ResponseEntity<ApiResponse<Object>> createMoMoPayment(@Valid @RequestBody PaymentRequest paymentRequest, HttpSession session) {
        try {
            // Lấy user ID từ session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Vui lòng đăng nhập để thanh toán"));
            }

            // Set user ID vào payment request
            paymentRequest.setUserId(userId);

            PaymentResponse response = paymentService.createMoMoPayment(paymentRequest);

            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response.getData()));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(response.getMessage()));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi tạo payment: " + e.getMessage()));
        }
    }

    /**
     * Lấy thông tin order theo ID
     * GET /api/payment/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        try {
            Optional<Order> orderOpt = paymentService.getOrderById(orderId);

            if (orderOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin order thành công", orderOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy thông tin order: " + e.getMessage()));
        }
    }

    /**
     * Lấy thông tin payment theo order ID
     * GET /api/payment/order/{orderId}/payment
     */
    @GetMapping("/order/{orderId}/payment")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            Optional<Payment> paymentOpt = paymentService.getPaymentByOrderId(orderId);

            if (paymentOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin payment thành công", paymentOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lỗi khi lấy thông tin payment: " + e.getMessage()));
        }
    }
}

/**
 * Webhook Controller để nhận callback từ MoMo
 * Phải tách riêng vì không cần auth
 */
@RestController
@RequestMapping("/payment/momo")
@CrossOrigin(origins = "*", maxAge = 3600)
class MoMoWebhookController {

    @Autowired
    private PaymentService paymentService;

    /**
     * MoMo IPN (Instant Payment Notification) endpoint
     * POST /payment/momo/notify
     */
    @PostMapping("/notify")
    public ResponseEntity<Map<String, Object>> handleMoMoIPN(@RequestBody Map<String, Object> ipnData) {
        try {
            System.out.println("Received MoMo IPN: " + ipnData);

            Map<String, Object> response = paymentService.handleMoMoIPN(ipnData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("resultCode", 99, "message", "Internal server error"));
        }
    }


}