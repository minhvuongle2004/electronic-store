package com.electronic.store.controller.user;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.Wishlist;
import com.electronic.store.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Wishlist>>> getUserWishlist(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<>(false, "Vui lòng đăng nhập để sử dụng chức năng này", null));
            }

            List<Wishlist> wishlist = wishlistService.getUserWishlist(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách yêu thích thành công", wishlist));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy danh sách yêu thích: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> addToWishlist(
            @PathVariable Long productId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<>(false, "Vui lòng đăng nhập để sử dụng chức năng này", null));
            }

            boolean success = wishlistService.addToWishlist(userId, productId);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Đã thêm vào danh sách yêu thích", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Sản phẩm đã có trong danh sách yêu thích", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi thêm vào danh sách yêu thích: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> removeFromWishlist(
            @PathVariable Long productId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<>(false, "Vui lòng đăng nhập để sử dụng chức năng này", null));
            }

            boolean success = wishlistService.removeFromWishlist(userId, productId);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Đã xóa khỏi danh sách yêu thích", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Sản phẩm không có trong danh sách yêu thích", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi xóa khỏi danh sách yêu thích: " + e.getMessage(), null));
        }
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
            @PathVariable Long productId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Kiểm tra thành công", false));
            }

            boolean inWishlist = wishlistService.isInWishlist(userId, productId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Kiểm tra thành công", inWishlist));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi kiểm tra danh sách yêu thích: " + e.getMessage(), null));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getWishlistCount(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Lấy số lượng thành công", 0L));
            }

            long count = wishlistService.getWishlistCount(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy số lượng thành công", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi lấy số lượng yêu thích: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearWishlist(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<>(false, "Vui lòng đăng nhập để sử dụng chức năng này", null));
            }

            wishlistService.clearWishlist(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đã xóa toàn bộ danh sách yêu thích", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Lỗi khi xóa danh sách yêu thích: " + e.getMessage(), null));
        }
    }
}