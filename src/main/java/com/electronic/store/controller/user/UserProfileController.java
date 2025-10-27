package com.electronic.store.controller.user;

import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.entity.User;
import com.electronic.store.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserProfileController {

    @Autowired
    private UserService userService;

    /**
     * Hiển thị trang thông tin cá nhân
     * GET /user/profile
     */
    @GetMapping("/profile")
    public String showProfile() {
        return "user/profile/index";
    }

    /**
     * API: Test endpoint
     * GET /user/profile/test
     */
    @GetMapping("/profile/test")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Map.of("status", "OK", "message", "Test successful"));
    }

    /**
     * API: Lấy thông tin người dùng hiện tại
     * GET /user/profile/data
     */
    @GetMapping("/profile/data")
    @ResponseBody
    public ResponseEntity<ApiResponse<User>> getCurrentUserData(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Vui lòng đăng nhập"));
        }

        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Người dùng không tồn tại"));
        }

        User user = userOpt.get();
        user.setPassword(null); // Ẩn password

        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", user));
    }

    /**
     * API: Cập nhật thông tin cá nhân
     * PUT /user/profile/update
     */
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @RequestBody Map<String, String> profileData,
            HttpSession session) {

        try {
            System.out.println("=== UPDATE PROFILE REQUEST ===");
            System.out.println("Profile data: " + profileData);

            Long userId = (Long) session.getAttribute("userId");
            System.out.println("User ID from session: " + userId);

            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Vui lòng đăng nhập"));
            }

            String fullName = profileData.get("fullName");
            String email = profileData.get("email");
            String phone = profileData.get("phone");
            String address = profileData.get("address");

            System.out.println("Extracted data - fullName: " + fullName + ", email: " + email);

            // Validate required fields
            if (fullName == null || fullName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Tên đầy đủ không được để trống"));
            }

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email không được để trống"));
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email không đúng định dạng"));
            }

            System.out.println("Calling userService.updateProfile...");
            ApiResponse<User> result = userService.updateProfile(userId, fullName, email, phone, address);
            System.out.println("Result from userService: " + result);

            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            System.out.println("Exception in updateProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * API: Đổi mật khẩu
     * PUT /user/profile/change-password
     */
    @PutMapping("/profile/change-password")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestBody Map<String, String> passwordData,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Vui lòng đăng nhập"));
        }

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");
        String confirmPassword = passwordData.get("confirmPassword");

        // Validate input
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Vui lòng nhập mật khẩu hiện tại"));
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Vui lòng nhập mật khẩu mới"));
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Xác nhận mật khẩu không khớp"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Mật khẩu mới phải có ít nhất 6 ký tự"));
        }

        ApiResponse<String> result = userService.changePassword(userId, currentPassword, newPassword);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * API: Kiểm tra email có khả dụng không
     * GET /user/profile/check-email?email=xxx
     */
    @GetMapping("/profile/check-email")
    @ResponseBody
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(
            @RequestParam String email,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Vui lòng đăng nhập"));
        }

        boolean isAvailable = userService.isEmailAvailableForUser(email, userId);

        return ResponseEntity.ok(
            ApiResponse.success("Kiểm tra email thành công", isAvailable)
        );
    }
}