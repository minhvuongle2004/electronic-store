package com.electronic.store.controller.user;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Promotion;
import com.electronic.store.entity.User;
import com.electronic.store.service.PromotionService;
import com.electronic.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/promotions")
public class UserPromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private UserService userService;

    /**
     * Lấy danh sách mã giảm giá có thể áp dụng cho đơn hàng
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailablePromotions(@RequestParam Double orderAmount) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                // Nếu chưa đăng nhập, vẫn trả về các promotion không cần user
                userId = 0L;
            }

            List<Promotion> availablePromotions = promotionService.getAvailablePromotionsForOrder(orderAmount, userId);

            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách mã giảm giá thành công", Map.of(
                "promotions", availablePromotions,
                "orderAmount", orderAmount
            )));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy danh sách mã giảm giá: " + e.getMessage()));
        }
    }

    /**
     * Áp dụng mã giảm giá
     */
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse> applyPromotion(
            @RequestBody Map<String, Object> request) {
        try {
            String promotionCode = (String) request.get("promotionCode");
            Double orderAmount = Double.valueOf(request.get("orderAmount").toString());

            Long userId = getCurrentUserId();
            if (userId == null) {
                userId = 0L; // Guest user
            }

            Double discountAmount = promotionService.applyPromotion(promotionCode, orderAmount, userId);
            Double finalAmount = orderAmount - discountAmount;

            return ResponseEntity.ok(ApiResponse.success("Áp dụng mã giảm giá thành công", Map.of(
                "promotionCode", promotionCode,
                "originalAmount", orderAmount,
                "discountAmount", discountAmount,
                "finalAmount", finalAmount
            )));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi áp dụng mã giảm giá: " + e.getMessage()));
        }
    }

    /**
     * Validate mã giảm giá
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validatePromotion(
            @RequestBody Map<String, Object> request) {
        try {
            String promotionCode = (String) request.get("promotionCode");
            Double orderAmount = Double.valueOf(request.get("orderAmount").toString());

            Long userId = getCurrentUserId();
            if (userId == null) {
                userId = 0L; // Guest user
            }

            boolean isValid = promotionService.validatePromotionForOrder(promotionCode, orderAmount, userId);

            if (isValid) {
                Double discountAmount = promotionService.applyPromotion(promotionCode, orderAmount, userId);
                return ResponseEntity.ok(ApiResponse.success("Mã giảm giá hợp lệ", Map.of(
                    "valid", true,
                    "discountAmount", discountAmount
                )));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Mã giảm giá không hợp lệ"));
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi kiểm tra mã giảm giá: " + e.getMessage()));
        }
    }

    /**
     * Lấy thông tin chi tiết của một mã giảm giá
     */
    @GetMapping("/detail/{code}")
    public ResponseEntity<ApiResponse> getPromotionDetail(@PathVariable String code) {
        try {
            var promotionOpt = promotionService.getPromotionByCode(code);
            if (promotionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Mã giảm giá không tồn tại"));
            }

            Promotion promotion = promotionOpt.get();
            if (!promotion.isActive()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Mã giảm giá không còn hiệu lực"));
            }

            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin mã giảm giá thành công", promotion));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy thông tin mã giảm giá: " + e.getMessage()));
        }
    }

    /**
     * Lấy ID của user hiện tại từ security context
     */
    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String username = auth.getName();
                User user = userService.findByUsername(username);
                return user != null ? user.getId() : null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}