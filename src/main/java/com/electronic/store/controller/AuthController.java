package com.electronic.store.controller;

import com.electronic.store.dto.request.LoginRequest;
import com.electronic.store.dto.request.SignUpRequest;
import com.electronic.store.dto.request.ResendVerificationRequest;
import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.dto.response.LoginResponse;
import com.electronic.store.entity.User;
import com.electronic.store.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Hiển thị thông tin login endpoint
     * GET /api/auth/login
     */
    @GetMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginInfo() {
        return ResponseEntity.ok(ApiResponse.success("Login endpoint info", "Use POST method with username/password"));
    }

    /**
     * Đăng nhập
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpSession session) {
        ApiResponse<LoginResponse> response = authService.login(loginRequest);

        if (response.isSuccess()) {
            // Lưu user ID vào session để dùng cho checkout
            LoginResponse loginData = response.getData();
            if (loginData != null && loginData.getId() != null) {
                session.setAttribute("userId", loginData.getId());

                // Tạo User object từ LoginResponse để lưu vào session
                User userInfo = new User();
                userInfo.setId(loginData.getId());
                userInfo.setUsername(loginData.getUsername());
                userInfo.setEmail(loginData.getEmail());
                userInfo.setFullName(loginData.getFullName());

                session.setAttribute("userInfo", userInfo);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }


    /**
     * Đăng ký user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        ApiResponse<String> response = authService.register(signUpRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Kiểm tra username có khả dụng không
     * GET /api/auth/check-username?username=abc
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailability(@RequestParam String username) {
        ApiResponse<Boolean> response = authService.checkUsernameAvailability(username);
        return ResponseEntity.ok(response);
    }

    /**
     * Kiểm tra email có khả dụng không
     * GET /api/auth/check-email?email=abc@test.com
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@RequestParam String email) {
        ApiResponse<Boolean> response = authService.checkEmailAvailability(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy thông tin user hiện tại (cần JWT token)
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        String username = authentication.getName();
        ApiResponse<User> response = authService.getCurrentUser(username);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test endpoint để kiểm tra server hoạt động
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is working!", "Server is running"));
    }

    /**
     * Test login endpoint - hardcoded for debugging
     * POST /api/auth/test-login
     */
    @PostMapping("/test-login")
    public ResponseEntity<ApiResponse<String>> testLogin() {
        try {
            // Test basic functionality
            return ResponseEntity.ok(ApiResponse.success("Test login endpoint working", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Test failed: " + e.getMessage()));
        }
    }

    /**
     * Tạo admin user đầu tiên (chỉ dùng khi setup)
     * POST /api/auth/setup-admin
     */
    @PostMapping("/setup-admin")
    public ResponseEntity<ApiResponse<String>> setupAdmin(@Valid @RequestBody SignUpRequest signUpRequest) {
        ApiResponse<String> response = authService.createAdminUser(signUpRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Đăng xuất - xóa thông tin khỏi session
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        session.removeAttribute("userId");
        session.removeAttribute("userInfo");
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", "Session cleared"));
    }

    /**
     * Xác thực email với token
     * GET /api/auth/verify-email?token=...
     */
    @GetMapping("/verify-email")
    public RedirectView verifyEmail(@RequestParam String token) {
        System.out.println("=== EMAIL VERIFICATION REQUEST ===");
        System.out.println("Token received: " + token);

        ApiResponse<String> response = authService.verifyEmail(token);

        System.out.println("Verification result: " + response.isSuccess());
        System.out.println("Verification message: " + response.getMessage());

        if (response.isSuccess()) {
            return new RedirectView("/auth/email-verification-success?message=" +
                   java.net.URLEncoder.encode(response.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        } else {
            return new RedirectView("/auth/check-email?verified=error&message=" +
                   java.net.URLEncoder.encode(response.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    /**
     * Gửi lại email xác thực
     * POST /api/auth/resend-verification
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(@RequestBody ResendVerificationRequest request) {
        ApiResponse<String> response = authService.resendEmailVerification(request.getEmail());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Kiểm tra trạng thái đăng nhập từ session
     * GET /api/auth/session-info
     */
    @GetMapping("/session-info")
    public ResponseEntity<ApiResponse<Object>> getSessionInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User userInfo = (User) session.getAttribute("userInfo");

        if (userId != null && userInfo != null) {
            return ResponseEntity.ok(ApiResponse.success("User đã đăng nhập", Map.of(
                "userId", userId,
                "userInfo", userInfo
            )));
        } else {
            return ResponseEntity.status(401).body(ApiResponse.error("Chưa đăng nhập"));
        }
    }

    /**
     * Test endpoint to set verification token for debugging
     * GET /api/auth/set-test-token?username=xxx
     */
    @GetMapping("/set-test-token")
    public ResponseEntity<ApiResponse<String>> setTestToken(@RequestParam String username) {
        try {
            ApiResponse<String> response = authService.setTestVerificationToken(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}