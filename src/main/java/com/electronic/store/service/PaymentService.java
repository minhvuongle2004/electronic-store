package com.electronic.store.service;

import com.electronic.store.config.MoMoConfig;
import com.electronic.store.dto.request.PaymentRequest;
import com.electronic.store.dto.response.PaymentResponse;
import com.electronic.store.entity.*;
import com.electronic.store.repository.*;
import com.electronic.store.util.MoMoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    @Autowired
    private MoMoConfig moMoConfig;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private UserPromotionRepository userPromotionRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tạo payment request đến MoMo
     */
    public PaymentResponse createMoMoPayment(PaymentRequest paymentRequest) {
        try {
            // 1. Tạo order trong database
            Order order = createOrder(paymentRequest);

            // 2. Tạo MoMo payment request
            Map<String, Object> momoRequest = buildMoMoRequest(order, paymentRequest);

            // 3. Gửi request đến MoMo Test Environment
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(momoRequest, headers);

            System.out.println("Sending request to MoMo test environment: " + moMoConfig.getEndpoint());
            System.out.println("Request payload: " + momoRequest);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    moMoConfig.getEndpoint(),
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            System.out.println("MoMo response: " + responseBody);

            if (responseBody != null && "0".equals(responseBody.get("resultCode").toString())) {
                // Success - MoMo trả về payment URL
                String payUrl = responseBody.get("payUrl").toString();
                String requestId = responseBody.get("requestId").toString();

                // Lưu thông tin payment
                Payment payment = new Payment();
                payment.setOrder(order);
                payment.setTransactionId(requestId);
                payment.setAmount(order.getTotalPrice());
                payment.setStatus(Payment.PaymentStatus.PENDING);
                payment.setCreatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                return PaymentResponse.success("Tạo payment thành công", Map.of(
                        "payUrl", payUrl,
                        "orderId", order.getId(),
                        "requestId", requestId
                ));

            } else {
                // Error from MoMo
                String message = responseBody != null ? responseBody.get("message").toString() : "Unknown error";
                return PaymentResponse.error("Lỗi từ MoMo: " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return PaymentResponse.error("Lỗi khi tạo payment: " + e.getMessage());
        }
    }

    /**
     * Xử lý MoMo IPN (Instant Payment Notification)
     */
    @Transactional
    public Map<String, Object> handleMoMoIPN(Map<String, Object> ipnData) {
        try {
            System.out.println("🔔 === RECEIVED MOMO IPN ===");
            System.out.println("IPN Data: " + ipnData);

            // 1. Validate signature
            if (!validateMoMoSignature(ipnData)) {
                System.err.println("❌ Invalid MoMo signature");
                return Map.of("resultCode", 97, "message", "Invalid signature");
            }
            System.out.println("✅ MoMo signature validated");

            // 2. Get order information
            String orderId = ipnData.get("orderId").toString();
            String resultCode = ipnData.get("resultCode").toString();
            String transId = ipnData.get("transId") != null ? ipnData.get("transId").toString() : "";

            System.out.println("Processing IPN for Order: " + orderId + ", ResultCode: " + resultCode);

            Optional<Order> orderOpt = orderRepository.findByOrderIdWithItems(orderId);
            if (orderOpt.isEmpty()) {
                System.err.println("❌ Order not found: " + orderId);
                return Map.of("resultCode", 99, "message", "Order not found");
            }

            Order order = orderOpt.get();
            System.out.println("✅ Found order: " + order.getId() + " with " +
                             (order.getOrderItems() != null ? order.getOrderItems().size() : 0) + " items");

            // 3. Update payment status
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(order.getId());
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                System.out.println("✅ Found payment: " + payment.getId() + " with status: " + payment.getStatus());

                if ("0".equals(resultCode)) {
                    // Payment success
                    System.out.println("💰 Processing successful payment for order: " + orderId);
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);
                    payment.setTransactionId(transId);
                    order.setStatus(Order.OrderStatus.PENDING); // Keep as PENDING, admin will change to SHIPPED later
                    order.setPaymentStatus(Order.PaymentStatus.PAID);

                    // Giảm stock của các sản phẩm
                    updateProductStock(order);

                    // Lưu thông tin sử dụng promotion vào user_promotions
                    saveUserPromotionUsage(order);

                    System.out.println("✅ Payment processing completed successfully for order: " + orderId);

                } else {
                    // Payment failed
                    System.out.println("❌ Payment failed for order: " + orderId + ", resultCode: " + resultCode);
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    order.setStatus(Order.OrderStatus.CANCELED);
                    order.setPaymentStatus(Order.PaymentStatus.FAILED);
                }

                paymentRepository.save(payment);
                orderRepository.save(order);
                System.out.println("💾 Saved payment and order status updates");
            } else {
                System.err.println("❌ Payment not found for order: " + order.getId());
            }

            return Map.of("resultCode", 0, "message", "Success");

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("resultCode", 99, "message", "Internal server error");
        }
    }

    /**
     * Tạo order từ payment request
     */
    private Order createOrder(PaymentRequest paymentRequest) {
        // Get user
        Optional<User> userOpt = userRepository.findById(paymentRequest.getUserId());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(paymentRequest.getTotalAmount());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentMethod(Order.PaymentMethod.MOMO);
        order.setPaymentStatus(Order.PaymentStatus.UNPAID);

        // Generate unique order ID
        String orderId = "ORDER_" + System.currentTimeMillis() + "_" + user.getId();
        order.setOrderId(orderId);

        // Set shipping information
        order.setShippingName(paymentRequest.getShippingName());
        order.setShippingPhone(paymentRequest.getShippingPhone());
        order.setShippingAddress(paymentRequest.getShippingAddress());

        // Set promotion information
        if (paymentRequest.getPromotionCode() != null && !paymentRequest.getPromotionCode().trim().isEmpty()) {
            order.setPromotionCode(paymentRequest.getPromotionCode());
            order.setDiscountAmount(paymentRequest.getDiscountAmount() != null ? paymentRequest.getDiscountAmount() : 0.0);
        }

        order = orderRepository.save(order);

        // Create order items
        for (PaymentRequest.CartItem cartItem : paymentRequest.getItems()) {
            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Product not found: " + cartItem.getProductId());
            }
            Product product = productOpt.get();

            // Validate stock
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItemRepository.save(orderItem);

            // DO NOT update stock here - will be updated after payment success
            System.out.println("=== STOCK WILL BE UPDATED AFTER PAYMENT SUCCESS ===");
            System.out.println("Product: " + product.getName() +
                             " | Current Stock: " + product.getStock() +
                             " | Will be reduced by: " + cartItem.getQuantity() + " after payment");
        }

        return order;
    }

    /**
     * Build MoMo payment request
     */
    private Map<String, Object> buildMoMoRequest(Order order, PaymentRequest paymentRequest) {
        String orderId = order.getOrderId();
        String requestId = MoMoUtils.generateRequestId();
        String amount = String.valueOf(order.getTotalPrice().longValue());
        String orderInfo = "Thanh toán đơn hàng " + orderId;
        String extraData = "";
        String requestType = "payWithATM"; // Use ATM/Card form instead of QR code

        // Create raw signature
        String rawSignature = MoMoUtils.createRawSignature(
                moMoConfig.getAccessKey(),
                amount,
                extraData,
                moMoConfig.getNotifyUrl(),
                orderId,
                orderInfo,
                moMoConfig.getPartnerCode(),
                moMoConfig.getRedirectUrl(),
                requestId,
                requestType
        );

        // Create signature
        String signature = MoMoUtils.createSignature(rawSignature, moMoConfig.getSecretKey());

        // Build request
        Map<String, Object> request = new HashMap<>();
        request.put("partnerCode", moMoConfig.getPartnerCode());
        request.put("partnerName", "Electronic Store");
        request.put("storeId", "ElectronicStore");
        request.put("requestId", requestId);
        request.put("amount", amount);
        request.put("orderId", orderId);
        request.put("orderInfo", orderInfo);
        request.put("redirectUrl", moMoConfig.getRedirectUrl());
        request.put("ipnUrl", moMoConfig.getNotifyUrl());
        request.put("lang", "vi");
        request.put("extraData", extraData);
        request.put("requestType", requestType);
        request.put("signature", signature);

        return request;
    }

    /**
     * Validate MoMo IPN signature
     */
    private boolean validateMoMoSignature(Map<String, Object> ipnData) {
        try {
            String signature = ipnData.get("signature").toString();

            String rawSignature = MoMoUtils.createIpnRawSignature(
                    moMoConfig.getAccessKey(),
                    ipnData.get("amount").toString(),
                    ipnData.get("extraData").toString(),
                    ipnData.get("message").toString(),
                    ipnData.get("orderId").toString(),
                    ipnData.get("orderInfo").toString(),
                    ipnData.get("orderType").toString(),
                    ipnData.get("partnerCode").toString(),
                    ipnData.get("payType").toString(),
                    ipnData.get("requestId").toString(),
                    ipnData.get("responseTime").toString(),
                    ipnData.get("resultCode").toString(),
                    ipnData.get("transId") != null ? ipnData.get("transId").toString() : ""
            );

            return MoMoUtils.validateIpnSignature(signature, rawSignature, moMoConfig.getSecretKey());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật stock của sản phẩm sau khi thanh toán thành công
     */
    private void updateProductStock(Order order) {
        try {
            System.out.println("=== STARTING STOCK UPDATE FOR ORDER: " + order.getOrderId() + " ===");

            // Lấy OrderItems từ database để đảm bảo có đầy đủ dữ liệu
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

            if (orderItems == null || orderItems.isEmpty()) {
                System.err.println("No order items found for order: " + order.getOrderId());
                return;
            }

            System.out.println("Found " + orderItems.size() + " items to update stock for order " + order.getOrderId());

            for (OrderItem orderItem : orderItems) {
                Long productId = orderItem.getProduct().getId();

                // Lấy product từ database để có stock mới nhất
                Optional<Product> productOpt = productRepository.findById(productId);
                if (productOpt.isEmpty()) {
                    System.err.println("Product not found with ID: " + productId);
                    continue;
                }

                Product product = productOpt.get();
                int currentStock = product.getStock();
                int quantityOrdered = orderItem.getQuantity();
                int newStock = Math.max(0, currentStock - quantityOrdered);

                System.out.println("Updating stock for Product ID: " + productId +
                                 " | Name: " + product.getName() +
                                 " | Current Stock: " + currentStock +
                                 " | Ordered: " + quantityOrdered +
                                 " | New Stock: " + newStock);

                product.setStock(newStock);
                Product savedProduct = productRepository.save(product);

                System.out.println("✅ Stock updated successfully for product: " + product.getName() +
                                 " | New stock in DB: " + savedProduct.getStock());
            }

            System.out.println("=== COMPLETED STOCK UPDATE FOR ORDER: " + order.getOrderId() + " ===");
        } catch (Exception e) {
            System.err.println("❌ Error updating product stock for order " + order.getOrderId() + ": " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để không làm fail payment process
        }
    }

    /**
     * Lấy thông tin order theo ID
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Lấy thông tin payment theo order ID
     */
    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    /**
     * FALLBACK: Xử lý MoMo callback khi IPN không được gọi (localhost issue)
     * Bypass signature validation cho test environment
     */
    @Transactional
    public Map<String, Object> handleMoMoFallback(Map<String, Object> callbackData) {
        try {
            System.out.println("🔄 === HANDLING MOMO FALLBACK CALLBACK ===");
            System.out.println("Callback Data: " + callbackData);

            // Get order information (similar to IPN but skip signature validation)
            String orderId = callbackData.get("orderId").toString();
            String resultCode = callbackData.get("resultCode").toString();
            String transId = callbackData.get("transId") != null ? callbackData.get("transId").toString() : "";

            System.out.println("Processing fallback for Order: " + orderId + ", ResultCode: " + resultCode);

            Optional<Order> orderOpt = orderRepository.findByOrderIdWithItems(orderId);
            if (orderOpt.isEmpty()) {
                System.err.println("❌ Order not found: " + orderId);
                return Map.of("resultCode", 99, "message", "Order not found");
            }

            Order order = orderOpt.get();
            System.out.println("✅ Found order: " + order.getId() + " with " +
                             (order.getOrderItems() != null ? order.getOrderItems().size() : 0) + " items");

            // Update payment status (only if not already processed)
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(order.getId());
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();

                // Only process if payment is still PENDING (avoid duplicate processing)
                if (payment.getStatus() == Payment.PaymentStatus.PENDING && "0".equals(resultCode)) {
                    System.out.println("💰 Processing successful payment fallback for order: " + orderId);

                    // Update payment and order status
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);
                    payment.setTransactionId(transId);
                    order.setStatus(Order.OrderStatus.PENDING);
                    order.setPaymentStatus(Order.PaymentStatus.PAID);

                    // Trigger stock update and promotion processing
                    updateProductStock(order);
                    saveUserPromotionUsage(order);

                    // Save changes
                    paymentRepository.save(payment);
                    orderRepository.save(order);

                    System.out.println("✅ Fallback processing completed successfully for order: " + orderId);
                } else {
                    System.out.println("⚠️ Payment already processed or failed, skipping fallback processing");
                }
            } else {
                System.err.println("❌ Payment not found for order: " + order.getId());
            }

            return Map.of("resultCode", 0, "message", "Fallback processing success");

        } catch (Exception e) {
            System.err.println("❌ Error in fallback processing: " + e.getMessage());
            e.printStackTrace();
            return Map.of("resultCode", 99, "message", "Fallback processing error");
        }
    }

    /**
     * Lưu thông tin sử dụng promotion vào bảng user_promotions
     */
    private void saveUserPromotionUsage(Order order) {
        try {
            // Kiểm tra order có sử dụng promotion không
            if (order.getPromotionCode() != null && !order.getPromotionCode().trim().isEmpty()) {
                // Tìm promotion theo code
                Optional<Promotion> promotionOpt = promotionRepository.findByCode(order.getPromotionCode());

                if (promotionOpt.isPresent()) {
                    Promotion promotion = promotionOpt.get();
                    User user = order.getUser();

                    // Kiểm tra chưa có record trong user_promotions (để tránh duplicate)
                    if (!userPromotionRepository.existsByUserIdAndPromotionId(user.getId(), promotion.getId())) {
                        // Tạo UserPromotion record
                        UserPromotion userPromotion = new UserPromotion();
                        userPromotion.setUser(user);
                        userPromotion.setPromotion(promotion);
                        userPromotion.setOrder(order);
                        userPromotion.setUsedAt(LocalDateTime.now());

                        userPromotionRepository.save(userPromotion);

                        // Cập nhật usedCount của promotion
                        promotion.setUsedCount((promotion.getUsedCount() != null ? promotion.getUsedCount() : 0) + 1);
                        promotionRepository.save(promotion);

                        System.out.println("Saved user promotion usage: User " + user.getId() +
                                         " used promotion " + promotion.getCode() + " for order " + order.getOrderId());
                    } else {
                        System.out.println("User promotion already exists, skipping...");
                    }
                } else {
                    System.err.println("Promotion not found with code: " + order.getPromotionCode());
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving user promotion usage: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để không làm fail toàn bộ payment process
        }
    }
}