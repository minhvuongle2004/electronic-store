package com.electronic.store.service;

import com.electronic.store.dto.request.LoginRequest;
import com.electronic.store.dto.request.SignUpRequest;
import com.electronic.store.dto.response.ApiResponse;
import com.electronic.store.dto.response.LoginResponse;
import com.electronic.store.entity.User;
import com.electronic.store.repository.UserRepository;
import com.electronic.store.security.UserPrincipal;
// import com.electronic.store.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // @Autowired
    // private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${app.email.verification.token-expiry-minutes}")
    private int tokenExpiryMinutes;

    // @Autowired - Disabled because JWT is disabled
    // private JwtUtils jwtUtils;

    /**
     * Đăng nhập - Manual authentication without Spring Security
     */
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for username/email: {}", loginRequest.getUsername());

            // 1. Tìm user theo username hoặc email
            User user = userRepository.findByUsernameOrEmail(loginRequest.getUsername(), loginRequest.getUsername())
                    .orElse(null);

            if (user == null) {
                logger.error("User not found with username/email: {}", loginRequest.getUsername());
                return ApiResponse.error("Invalid username/email or password");
            }

            logger.info("Found user: {}, role: {}", user.getUsername(), user.getRole());

            // 2. Kiểm tra password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.error("Invalid password for user: {}", loginRequest.getUsername());
                return ApiResponse.error("Invalid username/email or password");
            }

            logger.info("Password verified for user: {}", user.getUsername());

            // 3. Kiểm tra user có bị block không
            if (user.getStatus() == User.Status.BLOCKED) {
                logger.error("User account is blocked: {}", loginRequest.getUsername());
                return ApiResponse.error("Tài khoản này đã bị khóa do hành động người dùng bất ổn");
            }

            // 3.5. Kiểm tra email đã được xác thực chưa (chỉ check nếu có email)
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && !user.isEmailVerified()) {
                logger.error("User email not verified: {}", loginRequest.getUsername());
                return ApiResponse.error("Vui lòng xác thực email trước khi đăng nhập. Kiểm tra hộp thư email của bạn.");
            }

            // 4. Tạo JWT token - DISABLED
            logger.info("JWT token generation disabled");
            // String jwt = jwtUtils.generateJwtToken(user.getUsername());
            String jwt = "session-based-auth"; // Dummy token since we use session

            // 5. Tạo LoginResponse
            logger.info("Creating LoginResponse for user: {}", user.getUsername());
            LoginResponse loginResponse = LoginResponse.from(jwt, user);
            logger.info("LoginResponse created successfully");

            logger.info("User {} logged in successfully", user.getUsername());

            return ApiResponse.success("Login successful", loginResponse);

        } catch (Exception e) {
            logger.error("Login error for username: {}", loginRequest.getUsername(), e);
            return ApiResponse.error("Login failed: " + e.getMessage());
        }
    }

    /**
     * Đăng ký
     */
    public ApiResponse<String> register(SignUpRequest signUpRequest) {
        try {
            // 1. Kiểm tra username đã tồn tại
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                return ApiResponse.error("Username is already taken!");
            }

            // 2. Kiểm tra email đã tồn tại
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                return ApiResponse.error("Email is already in use!");
            }

            // 3. Tạo user mới
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setFullName(signUpRequest.getFullName());
            user.setPhone(signUpRequest.getPhone());
            user.setAddress(signUpRequest.getAddress());
            user.setRole(User.Role.USER); // Mặc định là USER
            user.setStatus(User.Status.ACTIVE);

            // 4. Generate email verification token
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setTokenExpiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes));
            // verifyEmail is null = chưa verify

            // 5. Lưu user
            userRepository.save(user);

            // 6. Gửi email xác thực (chỉ gửi nếu có email)
            if (signUpRequest.getEmail() != null && !signUpRequest.getEmail().trim().isEmpty()) {
                try {
                    emailService.sendEmailVerification(user);
                    logger.info("Verification email sent to: {}", user.getEmail());
                } catch (Exception e) {
                    logger.error("Failed to send verification email to: {}", user.getEmail(), e);
                    // Không fail registration nếu email gửi lỗi
                }
            }

            logger.info("User {} registered successfully", user.getUsername());

            return ApiResponse.success("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");

        } catch (Exception e) {
            logger.error("Registration error for username: {}", signUpRequest.getUsername(), e);
            return ApiResponse.error("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Xác thực email với token
     */
    public ApiResponse<String> verifyEmail(String token) {
        try {
            logger.info("=== EMAIL VERIFICATION PROCESS START ===");
            logger.info("Token received: {}", token);

            if (token == null || token.trim().isEmpty()) {
                logger.error("Token is null or empty");
                return ApiResponse.error("Token xác thực không hợp lệ");
            }

            // Tìm user theo token
            logger.info("Looking for user with token: {}", token);
            User user = userRepository.findByVerificationToken(token);
            if (user == null) {
                logger.error("No user found with token: {}", token);
                return ApiResponse.error("Token xác thực không tồn tại hoặc đã được sử dụng");
            }

            logger.info("Found user: {} with email: {}", user.getUsername(), user.getEmail());

            // Kiểm tra token đã hết hạn chưa
            if (user.isTokenExpired()) {
                logger.error("Token has expired for user: {}", user.getUsername());
                return ApiResponse.error("Token xác thực đã hết hạn. Vui lòng yêu cầu gửi lại email xác thực");
            }

            // Kiểm tra user đã được verify chưa
            if (user.isEmailVerified()) {
                logger.info("User {} already verified", user.getUsername());
                return ApiResponse.error("Tài khoản đã được xác thực trước đó");
            }

            // Verify email
            logger.info("Setting email verification timestamp for user: {}", user.getUsername());
            user.setVerifyEmail(LocalDateTime.now());
            user.setVerificationToken(null); // Clear token
            user.setTokenExpiresAt(null); // Clear expiry

            logger.info("Saving user with verification timestamp");
            userRepository.save(user);
            logger.info("User saved successfully");

            logger.info("Email verified successfully for user: {}", user.getEmail());

            return ApiResponse.success("Email đã được xác thực thành công! Bạn có thể đăng nhập ngay bây giờ.");

        } catch (Exception e) {
            logger.error("Email verification error for token: {}", token, e);
            return ApiResponse.error("Lỗi xác thực email: " + e.getMessage());
        }
    }

    /**
     * Gửi lại email xác thực
     */
    public ApiResponse<String> resendEmailVerification(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return ApiResponse.error("Email không được để trống");
            }

            // Tìm user theo email
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("Không tìm thấy tài khoản với email này");
            }

            User user = userOptional.get();

            // Kiểm tra đã verify chưa
            if (user.isEmailVerified()) {
                return ApiResponse.error("Tài khoản đã được xác thực trước đó");
            }

            // Generate token mới
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setTokenExpiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes));

            userRepository.save(user);

            // Gửi email
            try {
                emailService.resendEmailVerification(user);
                logger.info("Resend verification email sent to: {}", user.getEmail());
            } catch (Exception e) {
                logger.error("Failed to resend verification email to: {}", user.getEmail(), e);
                return ApiResponse.error("Không thể gửi email xác thực. Vui lòng thử lại sau");
            }

            return ApiResponse.success("Email xác thực đã được gửi lại. Vui lòng kiểm tra hộp thư của bạn");

        } catch (Exception e) {
            logger.error("Resend verification error for email: {}", email, e);
            return ApiResponse.error("Lỗi gửi lại email xác thực: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra username có khả dụng không
     */
    public ApiResponse<Boolean> checkUsernameAvailability(String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return ApiResponse.success("Username availability checked", isAvailable);
    }

    /**
     * Kiểm tra email có khả dụng không
     */
    public ApiResponse<Boolean> checkEmailAvailability(String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return ApiResponse.success("Email availability checked", isAvailable);
    }

    /**
     * Lấy thông tin user hiện tại từ JWT token
     */
    public ApiResponse<User> getCurrentUser(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Ẩn password trước khi trả về
            user.setPassword(null);

            return ApiResponse.success("User info retrieved", user);

        } catch (Exception e) {
            logger.error("Error getting current user: {}", username, e);
            return ApiResponse.error("Failed to get user info: " + e.getMessage());
        }
    }

    /**
     * Tạo admin user (chỉ dùng cho setup ban đầu)
     */
    public ApiResponse<String> createAdminUser(SignUpRequest signUpRequest) {
        try {
            // Kiểm tra đã có admin chưa
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                return ApiResponse.error("Username is already taken!");
            }

            // Tạo admin user
            User admin = new User();
            admin.setUsername(signUpRequest.getUsername());
            admin.setEmail(signUpRequest.getEmail());
            admin.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            admin.setFullName(signUpRequest.getFullName());
            admin.setPhone(signUpRequest.getPhone());
            admin.setAddress(signUpRequest.getAddress());
            admin.setRole(User.Role.ADMIN); // Set ADMIN role
            admin.setStatus(User.Status.ACTIVE);

            userRepository.save(admin);

            logger.info("Admin user {} created successfully", admin.getUsername());

            return ApiResponse.success("Admin user created successfully!");

        } catch (Exception e) {
            logger.error("Error creating admin user: {}", signUpRequest.getUsername(), e);
            return ApiResponse.error("Failed to create admin user: " + e.getMessage());
        }
    }

    /**
     * Set test verification token for debugging
     */
    public ApiResponse<String> setTestVerificationToken(String username) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found: " + username);
            }

            User user = userOptional.get();

            // Set test token
            String testToken = "test-token-123";
            user.setVerificationToken(testToken);
            user.setTokenExpiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes));
            user.setVerifyEmail(null); // Reset verification status

            userRepository.save(user);

            logger.info("Test token set for user: {}, token: {}", username, testToken);

            return ApiResponse.success("Test token set successfully: " + testToken);

        } catch (Exception e) {
            logger.error("Error setting test token for user: {}", username, e);
            return ApiResponse.error("Failed to set test token: " + e.getMessage());
        }
    }
}