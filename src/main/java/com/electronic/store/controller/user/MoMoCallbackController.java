package com.electronic.store.controller.user;

import com.electronic.store.entity.Order;
import com.electronic.store.entity.Payment;
import com.electronic.store.repository.OrderRepository;
import com.electronic.store.repository.PaymentRepository;
import com.electronic.store.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoMoCallbackController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    /**
     * MoMo return URL endpoint
     * GET /payment/momo/return
     */
    @GetMapping("/payment/momo/return")
    @Transactional
    public String handleMoMoReturn(@RequestParam Map<String, String> params) {
        try {
            System.out.println("Received MoMo return: " + params);

            String resultCode = params.get("resultCode");
            String orderId = params.get("orderId");
            String transId = params.get("transId");
            String message = params.get("message");

            System.out.println("Processing payment for orderId: " + orderId + ", resultCode: " + resultCode);

            // Cập nhật payment status dựa trên resultCode
            updatePaymentStatus(orderId, transId, resultCode);

            // Redirect đến trang kết quả với thông tin payment
            String redirectUrl = "/user/payment/result?resultCode=" + resultCode +
                    "&orderId=" + orderId +
                    "&message=" + (message != null ? message : "");

            return "redirect:" + redirectUrl;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing MoMo callback: " + e.getMessage());
            return "redirect:/user/payment/result?resultCode=99&message=System error";
        }
    }

    /**
     * Cập nhật payment status dựa trên MoMo result code
     * FALLBACK: Cũng xử lý stock update nếu IPN không được gọi (do localhost)
     */
    private void updatePaymentStatus(String orderIdStr, String transId, String resultCode) {
        try {
            System.out.println("🔄 === PROCESSING MOMO RETURN CALLBACK ===");
            System.out.println("Order: " + orderIdStr + ", ResultCode: " + resultCode + ", TransId: " + transId);

            // Tìm order theo orderId string
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderIdStr);
            if (orderOpt.isEmpty()) {
                System.out.println("❌ Order not found: " + orderIdStr);
                return;
            }

            Order order = orderOpt.get();
            System.out.println("✅ Found order: " + order.getId());

            // Tìm payment của order này
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(order.getId());
            if (paymentOpt.isEmpty()) {
                System.out.println("❌ Payment not found for order: " + order.getId());
                return;
            }

            Payment payment = paymentOpt.get();
            System.out.println("✅ Found payment: " + payment.getId() + ", current status: " + payment.getStatus());

            // Cập nhật payment status dựa trên resultCode
            Payment.PaymentStatus newStatus;
            Order.PaymentStatus newOrderPaymentStatus;

            if ("0".equals(resultCode)) {
                // Thanh toán thành công
                System.out.println("💰 Processing successful payment return");
                newStatus = Payment.PaymentStatus.SUCCESS;
                newOrderPaymentStatus = Order.PaymentStatus.PAID;

                // 🔥 FALLBACK: Trigger stock update and promotion processing
                // Vì IPN có thể không được gọi (localhost issue)
                System.out.println("🔄 FALLBACK: Calling PaymentService IPN logic from return callback");

                // Simulate IPN data for processing
                Map<String, Object> ipnData = new HashMap<>();
                ipnData.put("orderId", orderIdStr);
                ipnData.put("resultCode", resultCode);
                ipnData.put("transId", transId != null ? transId : "");
                ipnData.put("message", "Success from return callback");
                ipnData.put("orderInfo", "Order " + orderIdStr);
                ipnData.put("amount", order.getTotalPrice().toString());
                ipnData.put("extraData", "");
                ipnData.put("orderType", "momo_wallet");
                ipnData.put("partnerCode", "MOMOBKUN20180529");
                ipnData.put("payType", "webApp");
                ipnData.put("requestId", "REQ_" + System.currentTimeMillis());
                ipnData.put("responseTime", String.valueOf(System.currentTimeMillis()));
                ipnData.put("signature", "mock_signature_for_fallback");

                // Call PaymentService handleMoMoIPN with bypass signature validation
                paymentService.handleMoMoFallback(ipnData);

            } else {
                // Thanh toán thất bại
                System.out.println("❌ Payment failed, resultCode: " + resultCode);
                newStatus = Payment.PaymentStatus.FAILED;
                newOrderPaymentStatus = Order.PaymentStatus.FAILED;
            }

            // Cập nhật transactionId nếu có
            if (transId != null && !transId.isEmpty()) {
                payment.setTransactionId(transId);
            }

            // Lưu thay đổi
            payment.setStatus(newStatus);
            paymentRepository.save(payment);

            order.setPaymentStatus(newOrderPaymentStatus);
            orderRepository.save(order);

            System.out.println("✅ Updated payment status to: " + newStatus + " for payment: " + payment.getId());
            System.out.println("✅ Updated order payment status to: " + newOrderPaymentStatus + " for order: " + order.getId());

        } catch (Exception e) {
            System.err.println("❌ Error updating payment status: " + e.getMessage());
            e.printStackTrace();
        }
    }
}